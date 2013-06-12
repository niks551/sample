/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign;

import java.util.Collection;
import java.util.Map;

import com.convert.analytics.Aggregation;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public interface CampaignActivityAnalytics {

    ListenableFuture<?> increment(String merchantKey, String campaignKey, long timestamp,
            Map<CampaignMetrics, Long> metrics)
            throws Exception;

    ListenableFuture<CampaignActivity> getCampaignMetrics(String merchantKey, String campaignKey, long start, long end,
            Aggregation aggregation) throws Exception;

    /**
     * @param merchantKey
     * @param campaignKey
     * @param start
     * @param end
     * @return
     */
    ListenableFuture<Long> getUniqueCampaignMetric(String merchantKey, String campaignKey, CampaignMetrics metric,
            long start, long end);

    /**
     * @param mKey
     * @param cKey
     * @param vKey
     * @param timestamp
     * @param metrics
     * @return
     */
    ListenableFuture<Object> incrementUnique(String mKey, String cKey, String vKey, long timestamp,
            Collection<CampaignMetrics> metrics);

}
