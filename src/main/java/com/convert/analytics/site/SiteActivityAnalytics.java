/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.site;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.convert.analytics.Aggregation;
import com.convert.rice.client.protocol.Response.IncResult;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public interface SiteActivityAnalytics {

    ListenableFuture<SiteActivity> getSiteMetrics(String merchantKey, long start, long end, Aggregation aggregation)
            throws IOException, Exception;

    ListenableFuture<IncResult> increment(String merchantKey, long timestamp, Map<SiteMetrics, Long> metrics)
            throws IOException, Exception;

    /**
     * @param merchantKey
     * @param visitorKey
     * @param timestamp
     * @param metrics
     * @return
     */
    ListenableFuture<Object> incrementUnique(String merchantKey, String visitorKey, long timestamp,
            Collection<SiteMetrics> metrics);

    /**
     * @param merchantKey
     * @param start
     * @param end
     * @param metric
     * @return
     */
    ListenableFuture<Long> getUniqueMetric(String merchantKey, long start, long end, SiteMetrics metric);

}
