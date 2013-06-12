/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign.messaging;

import java.util.Collection;
import java.util.Map;

import com.convert.analytics.Aggregation;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public interface MessagingCampaignAnalytics {

    ListenableFuture<Object> increment(String mKey, String cKey, String mailingKey, String eKey, boolean isControl,
            long timestamp, Map<MessagingCampaignMetric, Long> metrics) throws Exception;

    ListenableFuture<Object> incrementUnique(String mKey, String cKey, String mailingKey, String eKey, String vKey,
            boolean isControl, long timestamp, Collection<MessagingCampaignMetric> metrics) throws Exception;

    ListenableFuture<MessagingCampaignActivity> getCampaignMetrics(String mKey, String cKey, long start, long end,
            Aggregation aggregation) throws Exception;

    ListenableFuture<Long> getUniqueMetric(String mKey, String cKey, String mailingKey,
            String engagementKey, long start, long end, MessagingCampaignMetric metric);

}
