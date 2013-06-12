/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign.online;

import java.util.Collection;
import java.util.Map;

import com.convert.analytics.Aggregation;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public interface OnlineCampaignAnalytics {

    ListenableFuture<Object> increment(String mKey, String cKey, String eKey, boolean isControl, long timestamp,
            Map<OnlineCampaignMetric, Long> metrics) throws Exception;

    ListenableFuture<Object> incrementUnique(String mKey, String cKey, String eKey, String visitorKey,
            boolean isControl, long timestamp, Collection<OnlineCampaignMetric> metrics) throws Exception;

    ListenableFuture<OnlineCampaignActivity> getCampaignMetrics(String mKey, String cKey, long start, long end,
            Aggregation aggregation) throws Exception;

    ListenableFuture<Long> getUniqueMetric(String mKey, String cKey, String eKey, boolean isControl, long start,
            long end, OnlineCampaignMetric metric);

}
