/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign.online;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

/**
 * @auth Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class C2EActivity {

    private final String campaignKey;

    private final String engagementKey;

    private final String merchantKey;

    private final long start;

    private final long end;

    private final Map<OnlineCampaignMetric, long[][]> metrics;

    private long uniqueDeliveries;

    private long totalUniqueCampaignDeliveries;

    private long uniqueAccepts;

    private long totalUniqueCampaignAccepts;

    private final boolean isControl;

    private transient long impressions = -1L;

    private transient long clicks = -1L;

    private transient long goals = -1L;

    private transient double revenue = -1L;

    private transient long ordersDelivered = -1L;

    private transient long ordersAccepted = -1L;

    private transient long items = -1L;

    /**
     * This constructor should be used by packages that eventually set the unique delivery as well as the campaign
     * unique delivery.
     * 
     * @param mKey
     * @param cKey
     * @param eKey
     * @param isControl
     * @param start
     * @param end
     */
    protected C2EActivity(final String mKey, String cKey, String eKey, boolean isControl, long start, long end) {
        this.campaignKey = checkNotNull(cKey);
        this.merchantKey = checkNotNull(mKey);
        this.engagementKey = checkNotNull(eKey);
        this.start = start;
        this.end = end;
        this.isControl = isControl;
        this.metrics = newHashMapWithExpectedSize(OnlineCampaignMetric.values().length);
    }

    public C2EActivity(final String mKey, String cKey, String eKey, boolean isControl, long start,
            long end, Map<OnlineCampaignMetric, long[][]> metrics, long uniqueDeliveries, long totalCampaignDelivery,
            long uniqueAccepts, long totalUniqueCampaignAccepts) {
        this.campaignKey = checkNotNull(cKey);
        this.merchantKey = checkNotNull(mKey);
        this.engagementKey = checkNotNull(eKey);
        this.start = start;
        this.end = end;
        this.setUniqueDeliveries(uniqueDeliveries);
        this.metrics = metrics;
        this.setTotalUniqueCampaignDeliveries(totalCampaignDelivery);
        this.isControl = isControl;
        this.setUniqueAccepts(uniqueAccepts);
        this.setTotalUniqueCampaignAccepts(totalUniqueCampaignAccepts);
    }

    /**
     * @return the engagementKey
     */
    public String getEngagementKey() {
        return engagementKey;
    }

    /**
     * Returns the average accept rate as a percentage defined as <code> 100D * accepts/impressions </code>
     * 
     * @return the average accept rate as a percentage.
     */
    public double getTotalAverageAcceptPercentage() {
        long totalDelivered = this.getTotalImpressions();
        if (totalDelivered == 0) {
            return 0;
        }
        long totalAccepted = this.getTotalClicks();

        return 100D * totalAccepted / totalDelivered;
    }

    public double[][] getAverageAcceptPercentage() {
        long[][] impressions = getImpressions();
        long[][] clicks = getClicks();
        List<double[]> averageAcceptPercentage = newArrayListWithExpectedSize(clicks.length);
        Map<Long, Long> accepts = newHashMapWithExpectedSize(clicks.length);
        for (long[] accept : clicks) {
            accepts.put(accept[0], accept[1]);
        }
        for (long[] impression : impressions) {
            if (accepts.containsKey(impression[0])) {
                averageAcceptPercentage.add(new double[] { impression[0],
                        impression[1] == 0L ? 0L : 100D * accepts.get(impression[0]) / impression[1] });
            } else {
                averageAcceptPercentage.add(new double[] { impression[0], 0D });
            }
        }
        return averageAcceptPercentage.toArray(new double[0][]);
    }

    public double[][] getAverageOrderValue() {
        double[][] revenues = getRevenue();
        long[][] orders = getOrdersDelivered();
        Map<Double, Long> ordersMap = newHashMapWithExpectedSize(orders.length);
        for (long[] point : orders) {
            ordersMap.put((double) point[0], point[1]);
        }

        double[][] avgOrderValue = new double[revenues.length][];
        for (int i = 0; i < avgOrderValue.length; i++) {
            if (ordersMap.containsKey(revenues[i][0])) {
                long co = ordersMap.get(revenues[i][0]);
                if (0 != co) {
                    avgOrderValue[i] = new double[] { revenues[i][0], 0 == co ? 0 : revenues[i][1] / co };
                }
            }
        }
        return avgOrderValue;
    }

    public long[][] getClicks() {
        long[][] clicks = metrics.get(OnlineCampaignMetric.ACCEPTED);
        return null == clicks ? new long[0][] : clicks;
    }

    public double getClickThroughRate() {
        long totalImpressions = getTotalImpressions();
        if (0L == totalImpressions) {
            return 0D;
        }
        return 100D * getTotalClicks() / totalImpressions;
    }

    public long[][] getItems() {
        long[][] items = metrics.get(OnlineCampaignMetric.ITEMS);
        return null == items ? new long[0][] : items;
    }

    public long[][] getNormalizedItems() {
        return normalize(getImpressions(), getNormalizationMultiplier());
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    public long[][] getGoals() {
        long[][] goals = metrics.get(OnlineCampaignMetric.GOALS);
        return null == goals ? new long[0][] : goals;
    }

    public long[][] getImpressions() {
        long[][] impressions = metrics.get(OnlineCampaignMetric.DELIVERED);
        return null == impressions ? new long[0][] : impressions;

    }

    /**
     * @return the merchantKey
     */
    public String getMerchantKey() {
        return merchantKey;
    }

    public long[][] getMetric(OnlineCampaignMetric metric) {
        return metrics.get(metric);
    }

    Map<OnlineCampaignMetric, long[][]> getMetricsMap() {
        return this.metrics;
    }

    /**
     * @return the metrics
     */
    public Map<OnlineCampaignMetric, long[][]> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    public long[][] getOrdersDelivered() {
        long[][] ordersDelivered = metrics.get(OnlineCampaignMetric.ORDERS_DELIVERED);
        return null == ordersDelivered ? new long[0][] : ordersDelivered;
    }

    public long[][] getNormalizedOrdersDelivered() {
        return normalize(getOrdersDelivered(), getNormalizationMultiplier());
    }

    public long[][] getNormalizedOrdersAccepted() {
        return normalize(getOrdersAccepted(), getNormalizationMultiplier());
    }

    public long[][] getOrdersAccepted() {
        long[][] ordersAccepted = metrics.get(OnlineCampaignMetric.ORDERS_ACCEPTED);
        return null == ordersAccepted ? new long[0][] : ordersAccepted;
    }

    public double[][] getRevenue() {
        long[][] revenues = metrics.get(OnlineCampaignMetric.REVENUE);
        if (null == revenues) {
            return new double[0][];
        }
        List<double[]> result = newArrayListWithCapacity(revenues.length);
        for (long[] point : revenues) {
            result.add(new double[] { point[0], point[1] / 100D });
        }
        return result.toArray(new double[0][]);
    }

    public double[][] getNormalizedRevenue() {
        return normalize(getRevenue(), getNormalizationMultiplier());
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    public double getTotalAverageOrderValue() {
        long orders = getTotalOrdersDelivered();
        return 0 == orders ? 0 : getTotalRevenue() / getTotalOrdersDelivered();

    }

    public long getTotalClicks() {
        if (-1L == clicks) {
            clicks = sum(OnlineCampaignMetric.ACCEPTED);
        }
        return clicks;
    }

    public double getNetConversionRate() {
        if (0 == getUniqueAccepts()) {
            return 0D;
        }
        // orders accepted / number of accepts (click through)
        return 100D * getTotalOrdersAccepted() / getUniqueAccepts();
    }

    public double getGrossConversionRate() {
        if (0 == getUniqueDeliveries()) {
            return 0D;
        }
        // orders delivered / unique visitors
        return 100D * getTotalOrdersDelivered() / getUniqueDeliveries();
    }

    /**
     * @return
     */
    public long getTotalOrdersAccepted() {
        if (-1L == ordersAccepted) {
            ordersAccepted = sum(OnlineCampaignMetric.ORDERS_ACCEPTED);
        }
        return ordersAccepted;
    }

    /**
     * @return
     */
    public long getUniqueDeliveries() {
        return uniqueDeliveries;
    }

    /**
     * @return
     */
    public long getNormalizedUniqueDeliveries() {
        return (long) (getNormalizationMultiplier() * getUniqueDeliveries());
    }

    public long getTotalGoals() {
        if (-1L == goals) {
            goals = sum(OnlineCampaignMetric.GOALS);
        }
        return goals;
    }

    public long getTotalImpressions() {
        if (-1L == impressions) {
            impressions = sum(OnlineCampaignMetric.DELIVERED);
        }
        return impressions;
    }

    public long getTotalOrdersDelivered() {
        if (-1L == ordersDelivered) {
            ordersDelivered = sum(OnlineCampaignMetric.ORDERS_DELIVERED);
        }
        return ordersDelivered;
    }

    public long getNormalizedTotalOrdersDelivered() {
        return (long) (getNormalizationMultiplier() * getTotalOrdersDelivered());
    }

    public long getNormalizedTotalOrdersAccepted() {
        return (long) (getNormalizationMultiplier() * getTotalOrdersAccepted());
    }

    public double getTotalRevenue() {
        if (-1D == revenue) {
            revenue = sum(OnlineCampaignMetric.REVENUE) / 100.0D;
        }
        return revenue;
    }

    public double getNormalizedTotalRevenue() {
        return getNormalizationMultiplier() * getTotalRevenue();
    }

    public long getTotalItems() {
        if (-1L == items) {
            items = sum(OnlineCampaignMetric.ITEMS);
        }
        return items;
    }

    public long getNormalizedTotalItems() {
        return (long) (getNormalizationMultiplier() * getTotalItems());
    }

    /**
     * @return the campaignKey
     */
    public String getCampaignKey() {
        return campaignKey;
    }

    public double getNormalizationFactor() {
        if (0 == getTotalUniqueCampaignDeliveries()) {
            return 0D;
        }
        return (double) getUniqueDeliveries() / getTotalUniqueCampaignDeliveries();
    }

    public double getNormalizationMultiplier() {
        double normalizationFactor = getNormalizationFactor();
        if (0D == normalizationFactor) {
            return 0D;
        }
        return 1D / normalizationFactor;
    }

    public long getTotalUniqueCampaignDeliveries() {
        return totalUniqueCampaignDeliveries;
    }

    private long sum(OnlineCampaignMetric metric) {
        long[][] datapoints = metrics.get(metric);

        return null == datapoints ? 0L : sum(datapoints);
    }

    private long sum(long[][] as) {
        long sum = 0;
        for (long[] a : as) {
            sum += a[1];
        }
        return sum;
    }

    @VisibleForTesting
    static double[][] normalize(double[][] as, double normalizationMultiplier) {
        double[][] normalizedAs = new double[as.length][];
        for (int i = 0; i < as.length; i++) {
            normalizedAs[i] = new double[] { as[i][0], normalizationMultiplier * as[i][1] };
        }
        return normalizedAs;
    }

    @VisibleForTesting
    long[][] normalize(long[][] as, double normalizationMultiplier) {
        long[][] normalizedAs = new long[as.length][];
        for (int i = 0; i < as.length; i++) {
            normalizedAs[i] = new long[] { as[i][0], (long) (normalizationMultiplier * as[i][1]) };
        }
        return normalizedAs;
    }

    /**
     * @return the isControl
     */
    public boolean isControl() {
        return isControl;
    }

    /**
     * @param totalUniqueCampaignDeliveries
     *            the totalUniqueCampaignDeliveries to set
     */
    protected void setTotalUniqueCampaignDeliveries(long totalUniqueCampaignDeliveries) {
        this.totalUniqueCampaignDeliveries = totalUniqueCampaignDeliveries;
    }

    /**
     * @param uniqueDeliveries
     *            the uniqueDeliveries to set
     */
    protected void setUniqueDeliveries(long uniqueDeliveries) {
        this.uniqueDeliveries = uniqueDeliveries;
    }

    /**
     * @return the uniqueAccepts
     */
    public long getUniqueAccepts() {
        return uniqueAccepts;
    }

    /**
     * @return
     */
    public long getNormalizedUniqueAccepts() {
        return (long) (getNormalizationMultiplier() * getUniqueAccepts());
    }

    /**
     * @param uniqueAccepts
     *            the uniqueAccepts to set
     */
    void setUniqueAccepts(long uniqueAccepts) {
        this.uniqueAccepts = uniqueAccepts;
    }

    /**
     * @return the totalUniqueCampaignAccepts
     */
    public long getTotalUniqueCampaignAccepts() {
        return totalUniqueCampaignAccepts;
    }

    /**
     * @param totalUniqueCampaignAccepts
     *            the totalUniqueCampaignAccepts to set
     */
    void setTotalUniqueCampaignAccepts(long totalUniqueCampaignAccepts) {
        this.totalUniqueCampaignAccepts = totalUniqueCampaignAccepts;
    }

    /**
     * @return
     */
    public double getDeliveryPercentage() {
        return getTotalUniqueCampaignDeliveries() == 0 ? 0 : 100.0D * getUniqueDeliveries()
                / getTotalUniqueCampaignDeliveries();
    }
}
