/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class CampaignActivity implements Serializable {

    private static final long serialVersionUID = -1518878666723509611L;

    private final String campaignKey;

    private final String merchantKey;

    private final long start;

    private final long end;

    private final Map<CampaignMetrics, long[][]> metrics;

    private final long uniqueDeliveries;

    private transient long impressinos = -1L;

    private transient long clicks = -1L;

    private transient long goals = -1L;

    private transient double revenue = -1D;

    private transient long ordersDelivered = -1L;

    private transient long ordersAccepted = -1L;

    private transient long items = -1L;

    /**
     * 
     * @param mKey
     * @param cKey
     * @param start
     * @param end
     * @param metrics
     * @param uniqueDeliveries
     * @throws NullPointerException
     *             if cKey or mKey or metrics is null.
     */
    public CampaignActivity(final String mKey, final String cKey, long start, long end,
            Map<CampaignMetrics, long[][]> metrics, long uniqueDeliveries) {
        checkArgument(start <= end, String.format("start(%d) must be less than end(%d)", start, end));
        this.campaignKey = checkNotNull(cKey);
        this.merchantKey = checkNotNull(mKey);
        this.start = start;
        this.end = end;
        this.uniqueDeliveries = uniqueDeliveries;
        this.metrics = checkNotNull(metrics);
    }

    public double getAverageAcceptPercentage() {
        long totalDelivered = this.getTotalImpressions();
        if (totalDelivered == 0) {
            return 0;
        }
        long totalAccepted = this.getTotalClicks();

        return 100D * totalAccepted / totalDelivered;
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
                long order = ordersMap.get(revenues[i][0]);
                avgOrderValue[i] = new double[] { revenues[i][0], 0 == order ? 0 : revenues[i][1] / order };
            } else {
                avgOrderValue[i] = new double[] { revenues[i][0], 0D };
            }
        }
        return avgOrderValue;
    }

    public long[][] getClicks() {
        long[][] accepts = metrics.get(CampaignMetrics.ACCEPTED);
        return null == accepts ? new long[0][] : accepts;
    }

    public double getClickThroughRate() {
        long totalImpressions = getTotalImpressions();
        if (0L == totalImpressions) {
            return 0D;
        }
        return 100D * getTotalClicks() / totalImpressions;
    }

    public long[][] getItems() {
        long[][] items = metrics.get(CampaignMetrics.ITEMS);
        return null == items ? new long[0][] : items;
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    public long[][] getGoals() {
        long[][] goals = metrics.get(CampaignMetrics.GOALS);
        return null == goals ? new long[0][] : goals;
    }

    public long[][] getImpressions() {
        long[][] delivered = metrics.get(CampaignMetrics.DELIVERED);
        return null == delivered ? new long[0][] : delivered;
    }

    /**
     * @return the merchantKey
     */
    public String getMerchantKey() {
        return merchantKey;
    }

    public long[][] getMetric(CampaignMetrics metric) {
        return metrics.get(metric);
    }

    /**
     * @return the metrics
     */
    public Map<CampaignMetrics, long[][]> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    public long[][] getOrdersDelivered() {
        long[][] ordersDelivered = metrics.get(CampaignMetrics.ORDERS_DELIVERED);
        return null == ordersDelivered ? new long[0][] : ordersDelivered;
    }

    public long[][] getOrdersAccepted() {
        long[][] ordersAccepted = metrics.get(CampaignMetrics.ORDERS_ACCEPTED);
        return null == ordersAccepted ? new long[0][] : ordersAccepted;
    }

    public double[][] getRevenue() {
        long[][] rs = metrics.get(CampaignMetrics.REVENUE);
        if (null == rs) {
            return new double[0][];
        }
        List<double[]> result = newArrayListWithCapacity(rs.length);
        for (long[] point : rs) {
            result.add(new double[] { point[0], point[1] / 100D });
        }
        return result.toArray(new double[0][]);
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    public double getTotalAverageOrderValue() {
        long orders = getTotalOrdersDelivered();
        if (0L == orders) {
            return 0D;
        }
        return 0 == orders ? 0 : getTotalRevenue() / getTotalOrdersDelivered();

    }

    public long getTotalClicks() {
        if (-1 == clicks) {
            clicks = sum(CampaignMetrics.ACCEPTED);
        }
        return clicks;
    }

    public long getTotallItems() {
        if (-1L == items) {
            items = sum(CampaignMetrics.ITEMS);
        }
        return items;
    }

    public double getNetConversionRate() {
        // orders accepted / number of accepts (click through)
        long totalClicks = getTotalClicks();
        if (0L == totalClicks) {
            return 0D;
        }
        return 100D * getTotalOrdersAccepted() / totalClicks;
    }

    public double getGrossConversionRate() {
        long uniqueDeliveries = getUniqueDeliveries();
        if (0L == uniqueDeliveries) {
            return 0D;
        }
        // orders delivered / unique visitors
        return 100D * getTotalOrdersDelivered() / uniqueDeliveries;
    }

    /**
     * @return
     */
    private long getTotalOrdersAccepted() {
        if (-1L == ordersAccepted) {
            ordersAccepted = sum(CampaignMetrics.ORDERS_ACCEPTED);
        }
        return ordersAccepted;
    }

    /**
     * @return
     */
    public long getUniqueDeliveries() {
        return uniqueDeliveries;
    }

    public long getTotalGoals() {
        if (-1L == goals) {
            goals = sum(CampaignMetrics.GOALS);
        }
        return goals;
    }

    public long getTotalImpressions() {
        if (-1L == impressinos) {
            impressinos = sum(CampaignMetrics.DELIVERED);
        }
        return impressinos;
    }

    public long getTotalOrdersDelivered() {
        if (-1L == ordersDelivered) {
            ordersDelivered = sum(CampaignMetrics.ORDERS_DELIVERED);
        }
        return ordersDelivered;
    }

    public double getTotalRevenue() {
        if (-1D == revenue) {
            revenue = sum(CampaignMetrics.REVENUE) / 100.0D;
        }
        return revenue;
    }

    public long getTotalItems() {
        if (0 == items) {
            items = sum(CampaignMetrics.ITEMS);
        }
        return items;
    }

    private long sum(CampaignMetrics metric) {
        long[][] datapoints = metrics.get(metric);
        if (null == datapoints) {
            return 0L;
        }
        long sum = 0;
        for (long[] datapoint : datapoints) {
            sum += datapoint[1];
        }
        return sum;
    }

    /**
     * @return the campaignKey
     */
    public String getCampaignKey() {
        return campaignKey;
    }
}
