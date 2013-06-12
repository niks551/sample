/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign.online;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;

import com.convert.analytics.Aggregation;
import com.convert.analytics.uniques.Requests;
import com.convert.rice.client.RiceClient;
import com.convert.rice.client.protocol.Point;
import com.convert.rice.client.protocol.Request;
import com.convert.rice.client.protocol.Request.Get;
import com.convert.rice.client.protocol.Request.Increment;
import com.convert.rice.client.protocol.Response;
import com.convert.rice.client.protocol.Response.GetResult;
import com.convert.rice.client.protocol.Response.GetResult.Metric;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class DefaultOnlineCampaignAnalytics implements OnlineCampaignAnalytics {

    /**
     * 
     */
    private static final String _ = "_";

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors
            .newCachedThreadPool());

    private static final String C2E_ACTIVITY = "online_campaign";

    private final RiceClient client;

    private final Requests requests;

    public DefaultOnlineCampaignAnalytics(RiceClient client, Requests r) {
        this.client = client;
        this.requests = r;
    }

    @Override
    public ListenableFuture<Object> increment(String mKey, String cKey, String eKey, boolean isControl, long timestamp,
            Map<OnlineCampaignMetric, Long> metrics) throws Exception {
        checkNotNull(mKey);
        checkNotNull(cKey);
        checkNotNull(eKey);
        checkNotNull(metrics);
        Increment.Builder incBuilder = Increment.newBuilder().setType(C2E_ACTIVITY).setKey(getKey(mKey, cKey))
                .setTimestamp(timestamp);
        for (Entry<OnlineCampaignMetric, Long> entry : metrics.entrySet()) {
            String key = eKey + _ + (isControl ? "c" : "t") + _ + entry.getKey().getShortUniqueName();
            Long value = entry.getValue();
            incBuilder.addMetrics(Increment.Metric
                    .newBuilder()
                    .setKey(key)
                    .setValue(value));
        }
        ListenableFuture<Response> future = client.send(Request.newBuilder().addInc(incBuilder).build());
        // Ugly shit.
        return Futures.transform(future, new Function<Response, Object>() {

            @Override
            public Object apply(Response input) {
                return input;
            }
        });
    }

    @Override
    public ListenableFuture<OnlineCampaignActivity> getCampaignMetrics(final String mKey, final String cKey,
            final long start, final long end, Aggregation aggregation) throws Exception {

        Get get = Get.newBuilder().setType(C2E_ACTIVITY).setKey(getKey(mKey, cKey)).setStart(start).setEnd(end)
                .addMapReduce(aggregation.getMR()).build();
        final ListenableFuture<Response> getResult = client.send(Request.newBuilder().addGet(get).build());
        Function<Response, OnlineCampaignActivity> transform = new Function<Response, OnlineCampaignActivity>() {

            @Override
            public OnlineCampaignActivity apply(Response input) {
                // We must have 1 GetResult.
                GetResult result = input.getGetResult(0);
                Map<String, C2EActivity> c2eActivities = new HashMap<String, C2EActivity>(3); // on average campaigns
                                                                                              // have 2 engagements.
                for (Metric metric : result.getMetricsList()) {
                    String[] nameElements = metric.getName().split(_);
                    String eKey = getEkey(nameElements);
                    boolean isControl = getIsControl(nameElements);
                    String shortMeticName = getMetricShortName(nameElements);
                    OnlineCampaignMetric c2eMetric = OnlineCampaignMetric.fromShortName(shortMeticName);
                    if (!c2eActivities.containsKey(eKey + _ + (isControl ? '1' : '0'))) {
                        c2eActivities.put(eKey + _ + (isControl ? '1' : '0'),
                                new C2EActivity(mKey, cKey, eKey, isControl, start, end));
                    }

                    long[][] points = new long[metric.getPointsCount()][];
                    for (int i = 0; i < points.length; i++) {
                        Point point = metric.getPoints(i);
                        points[i] = new long[] { point.getStart(), point.getValue() };
                    }
                    c2eActivities.get(eKey + _ + (isControl ? '1' : '0')).getMetricsMap().put(c2eMetric, points);
                }
                long totalUniqueDeliveries = 0;
                long totalUniqueAccepts = 0;
                for (C2EActivity c2ea : c2eActivities.values()) {
                    long uniqueDeliveries = Futures.getUnchecked(getUniqueDeliveries(mKey, cKey,
                            c2ea.getEngagementKey(), c2ea.isControl(), start, end));

                    long uniqueAccepts = Futures.getUnchecked(getUniqueMetric(mKey, cKey,
                            c2ea.getEngagementKey(), c2ea.isControl(), start, end, OnlineCampaignMetric.ACCEPTED));
                    c2ea.setUniqueDeliveries(uniqueDeliveries);
                    c2ea.setUniqueAccepts(uniqueAccepts);
                    totalUniqueDeliveries += uniqueDeliveries;
                    totalUniqueAccepts += uniqueAccepts;
                }
                for (C2EActivity c2ea : c2eActivities.values()) {
                    c2ea.setTotalUniqueCampaignDeliveries(totalUniqueDeliveries);
                    c2ea.setTotalUniqueCampaignAccepts(totalUniqueAccepts);
                }
                return new OnlineCampaignActivity(mKey, cKey, start, end, c2eActivities);
            }
        };

        return Futures.transform(getResult, transform, executor);
    }

    @Override
    public ListenableFuture<Object> incrementUnique(String mKey, String cKey, String eKey, String vKey,
            boolean isControl, long timestamp, Collection<OnlineCampaignMetric> metrics) {
        checkNotNull(mKey);
        checkNotNull(cKey);
        checkNotNull(eKey);
        checkNotNull(vKey);
        checkNotNull(metrics);
        List<ListenableFuture<?>> results = new ArrayList<ListenableFuture<?>>(metrics.size());
        for (OnlineCampaignMetric metric : metrics) {
            String key = getKey(mKey, cKey, eKey, isControl, metric);
            results.add(requests.newInc(key, timestamp, vKey.getBytes()));
        }
        return (ListenableFuture) Futures.allAsList(results);
    }

    @Override
    public ListenableFuture<Long> getUniqueMetric(String mKey, String cKey, String eKey, boolean isControl, long start,
            long end, OnlineCampaignMetric metric) {
        String key = getKey(mKey, cKey, eKey, isControl, metric);
        return requests.newEstimate(key, start, end);
    }

    @VisibleForTesting
    ListenableFuture<Long> getUniqueDeliveries(String merchantKey, String campaignKey,
            String engagementKey, boolean isControl, long start, long end) {
        String key = getKey(merchantKey, campaignKey, engagementKey, isControl, OnlineCampaignMetric.DELIVERED);
        return requests.newEstimate(key, start, end);

    }

    @VisibleForTesting
    String getKey(String mKey, String campaignKey, String engagementKey, boolean isControl, OnlineCampaignMetric metric) {
        return mKey + ":" + campaignKey + ":" + engagementKey + ":" + (isControl ? "c" : "t") + ":"
                + metric.getShortUniqueName();
    }

    /**
     * @param merchantKey
     * @param campaignKey
     * @return
     */
    private String getKey(String merchantKey, String campaignKey) {
        return campaignKey + _ + merchantKey;
    }

    private String getMetricShortName(String[] nameElements) {
        return nameElements[2];
    }

    private boolean getIsControl(String[] nameElements) {
        return nameElements[1].equals("c");
    }

    private String getEkey(String[] nameElements) {
        return nameElements[0];
    }

}
