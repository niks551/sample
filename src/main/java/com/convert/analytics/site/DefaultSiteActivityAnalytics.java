/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.site;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.getUnchecked;

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
import com.convert.rice.client.protocol.Response.IncResult;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class DefaultSiteActivityAnalytics implements SiteActivityAnalytics {

    private static final Gson gson = new Gson();

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors
            .newCachedThreadPool());

    private static final String ACTIVITY = "activity";

    private final RiceClient client;

    private final Requests requests;

    public DefaultSiteActivityAnalytics(RiceClient client, Requests requests) {
        this.client = client;
        this.requests = requests;
    }

    @Override
    public ListenableFuture<SiteActivity> getSiteMetrics(final String mKey, final long start, final long end,
            Aggregation aggregation)
            throws Exception {

        Get get = Get.newBuilder().setType(ACTIVITY).setKey(mKey).setStart(start).setEnd(end)
                .addMapReduce(aggregation.getMR()).build();
        final ListenableFuture<Response> future = client.send(Request.newBuilder().addGet(get).build());
        final ListenableFuture<Long> uniqueControl = getUniqueVisitor(mKey, start, end, true);
        final ListenableFuture<Long> uniqueTreatment = getUniqueVisitor(mKey, start, end, false);
        return Futures.transform(future,
                new Function<Response, SiteActivity>() {

                    @Override
                    public SiteActivity apply(Response input) {
                        GetResult result = input.getGetResult(0);

                        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>(3);
                        for (Metric metric : result.getMetricsList()) {
                            String name = metric.getName();
                            long[][] points = new long[metric.getPointsCount()][];
                            for (int i = 0; i < points.length; i++) {
                                Point point = metric.getPoints(i);
                                points[i] = new long[] { point.getStart(), point.getValue() };
                            }
                            for (SiteMetrics siteMetric : SiteMetrics.values()) {
                                if (name.equals(siteMetric.getShortUniqueName())) {
                                    metrics.put(siteMetric, points);
                                    break;
                                }
                            }
                        }
                        return new SiteActivity(mKey, start, end, metrics, getUnchecked(uniqueControl),
                                getUnchecked(uniqueTreatment));
                    }
                }, executor);

    }

    /**
     * @param mKey
     * @param start
     * @param end
     * @param isControl
     * @return
     */
    @VisibleForTesting
    ListenableFuture<Long> getUniqueVisitor(String mKey, long start, long end, boolean isControl) {
        return getUniqueMetric(mKey, start, end, isControl ? SiteMetrics.CONTROL_VISITORS
                : SiteMetrics.TREATMENT_VISITORS);
    }

    @Override
    public ListenableFuture<IncResult> increment(String mKey, long timestamp, Map<SiteMetrics, Long> metrics)
            throws Exception {
        checkNotNull(mKey);
        Increment.Builder incBuilder = Increment.newBuilder().setType(ACTIVITY).setKey(mKey)
                .setTimestamp(timestamp);
        for (Entry<SiteMetrics, Long> entry : metrics.entrySet()) {
            String key = entry.getKey().getShortUniqueName();
            Long value = entry.getValue();
            incBuilder.addMetrics(Increment.Metric
                    .newBuilder()
                    .setKey(key)
                    .setValue(value));
        }
        ListenableFuture<Response> result = client.send(Request.newBuilder().addInc(incBuilder).build());
        return Futures.transform(result, new Function<Response, IncResult>() {

            @Override
            public IncResult apply(Response input) {
                return input.getIncResult(0);
            }
        });

    }

    @Override
    public ListenableFuture<Object> incrementUnique(String merchantKey, String visitorKey, long timestamp,
            Collection<SiteMetrics> metrics) {
        checkNotNull(merchantKey);
        checkNotNull(visitorKey);
        List<ListenableFuture<?>> results = new ArrayList<ListenableFuture<?>>(metrics.size());
        for (SiteMetrics metric : metrics) {
            String key = getRowKey(merchantKey, metric);
            results.add(requests.newInc(key, timestamp, visitorKey.getBytes()));
        }
        return (ListenableFuture) Futures.allAsList(results);
    }

    @Override
    public ListenableFuture<Long> getUniqueMetric(String merchantKey, long start, long end, SiteMetrics metric) {
        String key = getRowKey(merchantKey, metric);
        return requests.newEstimate(key, start, end);
    }

    @VisibleForTesting
    String getRowKey(String merchantKey, SiteMetrics metric) {
        return merchantKey + ":" + metric.getShortUniqueName();
    }
}
