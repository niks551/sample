/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign;

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
public class DefaultCampaignActivityAnalytics implements CampaignActivityAnalytics {

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors
            .newCachedThreadPool());

    private static final String CAMPAIGN_ACTIVITY = "campaign_activity";

    private final RiceClient client;

    private final Requests requests;

    public DefaultCampaignActivityAnalytics(RiceClient client, Requests requests) {
        this.client = client;
        this.requests = requests;
    }

    @Override
    public ListenableFuture<CampaignActivity> getCampaignMetrics(final String mKey, final String cKey,
            final long start, final long end, Aggregation aggregation) throws Exception {
        Get get = Get.newBuilder().setType(CAMPAIGN_ACTIVITY).setKey(getKey(mKey, cKey)).setStart(start).setEnd(end)
                .addMapReduce(aggregation.getMR()).build();
        final ListenableFuture<Long> uniqueCampaignDeliveries = getUniqueCampaignDeliveries(mKey, cKey, start, end);
        final ListenableFuture<Response> future = client.send(Request.newBuilder().addGet(get).build());

        return Futures.transform(future,
                new Function<Response, CampaignActivity>() {

                    @Override
                    public CampaignActivity apply(Response input) {
                        GetResult result = input.getGetResult(0);
                        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>(result
                                .getMetricsCount());
                        for (Metric metric : result.getMetricsList()) {
                            String name = metric.getName();
                            long[][] points = new long[metric.getPointsCount()][];
                            for (int i = 0; i < points.length; i++) {
                                Point point = metric.getPoints(i);
                                points[i] = new long[] { point.getStart(), point.getValue() };
                            }
                            for (CampaignMetrics campaignMetric : CampaignMetrics.values()) {
                                if (name.equals(campaignMetric.getShortUniqueName())) {
                                    metrics.put(campaignMetric, points);
                                    break;
                                }
                            }
                        }
                        return new CampaignActivity(mKey, cKey, start, end, metrics, Futures
                                .getUnchecked(uniqueCampaignDeliveries));
                    }
                }, executor);

    }

    /**
     * @param mKey
     * @param cKey
     * @param start
     * @param end
     * @return
     */
    private ListenableFuture<Long> getUniqueCampaignDeliveries(String mKey, String cKey, long start, long end) {
        ListenableFuture<Long> control = getUniqueCampaignMetric(mKey, cKey, CampaignMetrics.UNIQUE_DELIVERED_CONTROL,
                start, end);
        ListenableFuture<Long> experiment = getUniqueCampaignMetric(mKey, cKey,
                CampaignMetrics.UNIQUE_DELIVERED_TREATMENT, start, end);

        @SuppressWarnings("unchecked")
        ListenableFuture<List<Long>> uniqueDeliveries = Futures.allAsList(control, experiment);
        return Futures.transform(uniqueDeliveries, new Function<List<Long>, Long>() {

            @Override
            public Long apply(List<Long> input) {
                return input.get(0) + input.get(1);
            }

        });

    }

    @Override
    public ListenableFuture<Long> getUniqueCampaignMetric(String merchantKey, String campaignKey,
            CampaignMetrics metric, long start, long end) {
        String rowKey = getRowKey(merchantKey, campaignKey, metric);
        return requests.newEstimate(rowKey, start, end);
    }

    @Override
    public ListenableFuture<?> increment(String mKey, String cKey, long timestamp, Map<CampaignMetrics, Long> metrics)
            throws Exception {
        checkNotNull(mKey);
        checkNotNull(cKey);
        checkNotNull(metrics);
        Increment.Builder incBuilder = Increment.newBuilder().setType(CAMPAIGN_ACTIVITY).setKey(getKey(mKey, cKey))
                .setTimestamp(timestamp);
        for (Entry<CampaignMetrics, Long> entry : metrics.entrySet()) {
            String key = entry.getKey().getShortUniqueName();
            Long value = entry.getValue();
            incBuilder.addMetrics(Increment.Metric
                    .newBuilder()
                    .setKey(key)
                    .setValue(value));
        }
        return client.send(Request.newBuilder().addInc(incBuilder).build());
    }

    @Override
    public ListenableFuture<Object> incrementUnique(String mKey, String cKey, String vKey, long timestamp,
            Collection<CampaignMetrics> metrics) {
        checkNotNull(mKey);
        checkNotNull(cKey);
        checkNotNull(vKey);
        checkNotNull(metrics);
        List<ListenableFuture<?>> results = new ArrayList<ListenableFuture<?>>(metrics.size());
        for (CampaignMetrics metric : metrics) {
            String key = getRowKey(mKey, cKey, metric);
            results.add(requests.newInc(key, timestamp, vKey.getBytes()));
        }
        return (ListenableFuture) Futures.allAsList(results);
    }

    public ListenableFuture<Long> getUniqueMetric(String mKey, String cKey, long start, long end, CampaignMetrics metric) {
        String key = getRowKey(mKey, cKey, metric);
        return requests.newEstimate(key, start, end);
    }

    /**
     * @param merchantKey
     * @param campaignKey
     * @return
     */
    private String getKey(String merchantKey, String campaignKey) {
        return campaignKey + "_" + merchantKey;
    }

    @VisibleForTesting
    String getRowKey(String merchantKey, String campaignKey, CampaignMetrics metric) {
        return merchantKey + ":" + campaignKey + ":" + metric.getShortUniqueName();
    }

}
