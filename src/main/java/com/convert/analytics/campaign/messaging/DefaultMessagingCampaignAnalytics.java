/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign.messaging;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.convert.analytics.Aggregation;
import com.convert.analytics.campaign.CampaignActivityAnalytics;
import com.convert.analytics.campaign.CampaignMetrics;
import com.convert.analytics.uniques.Requests;
import com.convert.rice.client.RiceClient;
import com.convert.rice.client.protocol.Point;
import com.convert.rice.client.protocol.Request;
import com.convert.rice.client.protocol.Request.Get;
import com.convert.rice.client.protocol.Request.Increment;
import com.convert.rice.client.protocol.Response;
import com.convert.rice.client.protocol.Response.GetResult;
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
public class DefaultMessagingCampaignAnalytics implements MessagingCampaignAnalytics {

    private static final String _ = "_";

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors
            .newCachedThreadPool());

    private static final String C2E2M_ACTIVITY = "messaging_campaign";

    private final RiceClient client;

    private final Requests requests;

    private CampaignActivityAnalytics ca;

    public DefaultMessagingCampaignAnalytics(RiceClient client, Requests requests, CampaignActivityAnalytics ca) {
        this.client = client;
        this.requests = requests;
        this.ca = ca;
    }

    @Override
    public ListenableFuture<Object> increment(String mKey, String cKey, String mailingKey, String eKey,
            boolean isControl, long timestamp, Map<MessagingCampaignMetric, Long> metrics) throws Exception {
        checkNotNull(mKey);
        checkNotNull(cKey);
        checkNotNull(mailingKey);
        checkNotNull(eKey);
        checkNotNull(metrics);
        Increment.Builder incBuilder = Increment.newBuilder().setType(C2E2M_ACTIVITY).setKey(getKey(mKey, cKey))
                .setTimestamp(timestamp);
        for (Entry<MessagingCampaignMetric, Long> entry : metrics.entrySet()) {
            String key = mailingKey + _ + eKey + _ + (isControl ? "c" : "t") + _ + entry.getKey().getShortUniqueName();
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
    public ListenableFuture<Object> incrementUnique(String mKey, String cKey, String mailingKey, String eKey,
            String vKey, boolean isControl, long timestamp, Collection<MessagingCampaignMetric> metrics)
            throws Exception {
        checkNotNull(mKey);
        checkNotNull(cKey);
        checkNotNull(mailingKey);
        checkNotNull(eKey);
        checkNotNull(vKey);
        checkNotNull(metrics);
        List<ListenableFuture<?>> results = new ArrayList<ListenableFuture<?>>(metrics.size());
        for (MessagingCampaignMetric metric : metrics) {
            String key = getKey(mKey, cKey, mailingKey, eKey, metric);
            results.add(requests.newInc(key, timestamp, vKey.getBytes()));
        }
        return (ListenableFuture) Futures.allAsList(results);
    }

    @Override
    public ListenableFuture<MessagingCampaignActivity> getCampaignMetrics(final String mKey, final String cKey,
            final long start, final long end, Aggregation aggregation) throws Exception {

        final ListenableFuture<Long> uniqueControlCampaignDeliveries = getUniqueCampaignDeliveries(mKey, cKey, true,
                start, end);
        final ListenableFuture<Long> uniqueTreatmentCampaignDeliveries = getUniqueCampaignDeliveries(mKey, cKey, true,
                start, end);

        Get get = Get.newBuilder().setType(C2E2M_ACTIVITY).setKey(getKey(mKey, cKey)).setStart(start).setEnd(end)
                .addMapReduce(aggregation.getMR()).build();
        final ListenableFuture<Response> getResult = client.send(Request.newBuilder().addGet(get).build());
        final Function<Response, MessagingCampaignActivity> transform = new Function<Response, MessagingCampaignActivity>() {

            @Override
            public MessagingCampaignActivity apply(Response input) {
                // We must have 1 get result
                GetResult result = input.getGetResult(0);

                Map<String, MailingActivity> mailingActivities = new HashMap<String, MailingActivity>(2); // so far we
                                                                                                          // have
                // most users use
                // 1 mailing per
                // campaign.
                for (GetResult.Metric metric : result.getMetricsList()) {
                    Map<String, C2E2MActivity> c2eActivities = null;
                    String[] nameElements = metric.getName().split(_);
                    String mailingKey = getMailingKey(nameElements);
                    if (!mailingActivities.containsKey(mailingKey)) {
                        c2eActivities = new HashMap<String, C2E2MActivity>();
                        mailingActivities.put(mailingKey, new MailingActivity(mKey, cKey, mailingKey, start, end,
                                c2eActivities));
                    } else {
                        c2eActivities = mailingActivities.get(mailingKey).getEngagementActivities();
                    }
                    String eKey = getEkey(nameElements);
                    boolean isControl = getIsControl(nameElements);
                    String shortMeticName = getMetricShortName(nameElements);
                    MessagingCampaignMetric c2eMetric = MessagingCampaignMetric.fromShortName(shortMeticName);
                    if (!c2eActivities.containsKey(eKey + _ + (isControl ? '1' : '0'))) {
                        c2eActivities.put(eKey + _ + (isControl ? '1' : '0'),
                                new C2E2MActivity(mKey, cKey, mailingKey, eKey, isControl, start, end));
                    }

                    long[][] points = new long[metric.getPointsCount()][];
                    for (int i = 0; i < points.length; i++) {
                        Point point = metric.getPoints(i);
                        points[i] = new long[] { point.getStart(), point.getValue() };
                    }
                    c2eActivities.get(eKey + _ + (isControl ? '1' : '0')).getMetricsMap().put(c2eMetric, points);
                }
                // Set unique counts.
                for (MailingActivity mailingActivity : mailingActivities.values()) {
                    for (C2E2MActivity c2ea : mailingActivity.getEngagementActivities().values()) {
                        long uniqueDeliveries = Futures.getUnchecked(getUniqueDeliveries(mKey, cKey,
                                c2ea.getMailingKey(), c2ea.getEngagementKey(), start, end));
                        long uniqueAccepts = Futures.getUnchecked(getUniqueMetric(mKey, cKey,
                                c2ea.getMailingKey(), c2ea.getEngagementKey(), start, end,
                                MessagingCampaignMetric.ACCEPTED));
                        c2ea.setUniqueDeliveries(uniqueDeliveries);
                        c2ea.setUniqueAccepts(uniqueAccepts);
                    }
                }
                for (MailingActivity mailingActivity : mailingActivities.values()) {
                    for (C2E2MActivity c2ea : mailingActivity.getEngagementActivities().values()) {
                        c2ea.setTotalUniqueCampaignDeliveries(mailingActivity.getTotalUniqueVisitors());
                        c2ea.setTotalUniqueCampaignAccepts(mailingActivity.getTotalUniqueAccepts());
                    }
                }
                MessagingCampaignActivity messagingCampaignActivity = new MessagingCampaignActivity(mKey, cKey, start,
                        end, mailingActivities);
                messagingCampaignActivity.setTotalUniqueControlVisitors(getUnchecked(uniqueControlCampaignDeliveries));
                messagingCampaignActivity
                        .setTotalUniqueTreatmentVisitors(getUnchecked(uniqueTreatmentCampaignDeliveries));
                return messagingCampaignActivity;
            }

        };

        return Futures.transform(getResult, transform, executor);
    }

    @VisibleForTesting
    Future<Long> getUniqueDeliveries(String mKey, String cKey, String mailingKey, String eKey, long start,
            long end) {
        return getUniqueMetric(mKey, cKey, mailingKey, eKey, start, end,
                MessagingCampaignMetric.DELIVERED);
    }

    @Override
    public ListenableFuture<Long> getUniqueMetric(String mKey, String cKey,
            String mailingKey, String eKey, long start, long end, MessagingCampaignMetric metric) {
        String key = getKey(mKey, cKey, mailingKey, eKey, metric);
        return requests.newEstimate(key, start, end);
    }

    /**
     * @param merchantKey
     * @param campaignKey
     * @return
     */
    private String getKey(String merchantKey, String campaignKey) {
        return campaignKey + _ + merchantKey;
    }

    @VisibleForTesting
    String getKey(String mKey, String campaignKey, String mailingKey, String engagementKey,
            MessagingCampaignMetric metric) {
        return mKey + ":" + campaignKey + ":" + mailingKey + ":" + engagementKey + ":" + metric.getShortUniqueName();
    }

    private String getMetricShortName(String[] nameElements) {
        return nameElements[3];
    }

    private boolean getIsControl(String[] nameElements) {
        return nameElements[2].equals("c");
    }

    private String getEkey(String[] nameElements) {
        return nameElements[1];
    }

    private String getMailingKey(String[] nameElements) {
        return nameElements[0];
    }

    public ListenableFuture<Long> getUniqueCampaignDeliveries(String merchantKey, String campaignKey,
            boolean isControl, long start,
            long end) {
        CampaignMetrics metric = isControl ? CampaignMetrics.UNIQUE_DELIVERED_CONTROL
                : CampaignMetrics.UNIQUE_DELIVERED_TREATMENT;
        return this.ca.getUniqueCampaignMetric(merchantKey, campaignKey, metric, start, end);
    }
}
