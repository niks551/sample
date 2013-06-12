/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.site;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static java.lang.Math.max;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

/**
 * This class corresponds to the site wide activity
 * 
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class SiteActivity implements Serializable {

    private static final long serialVersionUID = -8176389298697278283L;

    private final String merchantKey;

    private final long start;

    private final long end;

    private final Map<SiteMetrics, long[][]> metrics;

    private final long uniqueTreatment;

    private final long uniqueControl;

    private transient long impressinos = -1L;

    private transient long clicks = -1L;

    private transient long goals = -1L;

    private transient double controlRevenue = -1D;

    private transient double treatmentRevenue = -1D;

    private transient long controlOrders = -1L;

    private transient long treatmentOrders = -1L;

    private transient long treatmentItems = -1L;

    private transient long controlItems = -1;

    private transient double controlAbandonedRevenue = -1D;

    private transient double treatmentAbandonedRevenue = -1D;

    private transient long treatmentAbandonments = -1L;

    private transient long controlAbandonments = -1L;

    /**
     * Constructor.
     * 
     * @param merchantKey
     *            the merchant key.
     * @param start
     *            the start of the time period inclusive.
     * @param end
     *            the end of the period exclusive
     * @param metrics
     *            all of the metrics
     * @param uniqueControl
     *            the number of unique control visitors.
     * @param uniqueTreatment
     *            the number of unique treatment visitors.
     * @throws NullPointerException
     *             if merchantKey or metrics is null.
     * @throws IllegalArgumentException
     *             if start > end
     */
    public SiteActivity(final String merchantKey, long start, long end,
            Map<SiteMetrics, long[][]> metrics, long uniqueControl, long uniqueTreatment) {
        checkArgument(start <= end, String.format("start(%d) must be less than end(%d)", start, end));
        this.merchantKey = checkNotNull(merchantKey);
        this.start = start;
        this.end = end;
        this.uniqueControl = uniqueControl;
        this.uniqueTreatment = uniqueTreatment;
        this.metrics = checkNotNull(metrics);
    }

    /**
     * Add an implementation of getAverageAcceptPercentage <br>
     * The average accept percentage calculates the accept percentage as follows: <br>
     * If there are impressions at time t then we return the 100 * accepts / impressions <br>
     * If there were no accepts at time t then we return 0 <br>
     * If there were no impressions at time t then we omit that datapoint. <br>
     * 
     * @return a 2 dimensional array of doubles containing a {timestamp, value}
     */
    public double[][] getAverageAcceptPercentage() {
        long[][] impressions = getImpressions();
        long[][] clicks = getClicks();
        List<double[]> averageAcceptPercentage = newArrayListWithExpectedSize(clicks.length);
        Map<Long, Long> accepts = new HashMap<Long, Long>(clicks.length);
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

    public double[][] getAverageControlOrderValue() {
        double[][] crs = getControlRevenue();
        long[][] cos = getControlOrders();
        Map<Double, Long> coMap = newHashMapWithExpectedSize(cos.length);
        for (long[] point : cos) {
            coMap.put((double) point[0], point[1]);
        }

        double[][] avgControlOrderValue = new double[crs.length][];
        for (int i = 0; i < avgControlOrderValue.length; i++) {
            if (coMap.containsKey(crs[i][0])) {
                long co = coMap.get(crs[i][0]);
                avgControlOrderValue[i] = new double[] { crs[i][0], 0 == co ? 0 : crs[i][1] / co };
            }
        }
        return avgControlOrderValue;
    }

    public double[][] getAverageTreatmentOrderValue() {
        double[][] trs = getTreatmentRevenue();
        long[][] tos = getTreatmentOrders();
        Map<Double, Long> toMap = newHashMapWithExpectedSize(tos.length);
        for (long[] point : tos) {
            toMap.put((double) point[0], point[1]);
        }

        double[][] avgTreatmentOrderValue = new double[trs.length][];
        for (int i = 0; i < avgTreatmentOrderValue.length; i++) {
            if (toMap.containsKey(trs[i][0])) {
                long co = toMap.get(trs[i][0]);
                avgTreatmentOrderValue[i] = new double[] { trs[i][0], 0 == co ? 0D : trs[i][1] / co };
            }
        }
        return avgTreatmentOrderValue;
    }

    public double[][] getAverageOrderValue() {
        double[][] rs = getRevenue();
        long[][] os = getOrders();

        Map<Double, Long> oMap = newHashMapWithExpectedSize(os.length);
        for (long[] point : os) {
            oMap.put((double) point[0], point[1]);
        }

        double[][] avgOrderValue = new double[rs.length][];
        for (int i = 0; i < avgOrderValue.length; i++) {
            if (oMap.containsKey(rs[i][0])) {
                long o = oMap.get(rs[i][0]);
                avgOrderValue[i] = new double[] { rs[i][0], 0 == o ? 0 : rs[i][1] / o };
            }
        }
        return avgOrderValue;
    }

    public long[][] getClicks() {
        long[][] clicks = metrics.get(SiteMetrics.ACCEPTED);
        return null == clicks ? new long[0][] : clicks;
    }

    public double getControlConversionPercentage() {
        long uniqueControlVisitors = getUniqueControl();
        if (0 == uniqueControlVisitors) {
            return 0;
        }
        return 100D * getTotalControlOrders() / uniqueControlVisitors;
    }

    public long[][] getItems() {
        return combine(getControlItems(), getTreatmentItems());
    }

    public long[][] getAbandonments() {
        return combine(getControlAbandonments(), getTreatmentAbandonments());
    }

    public long getTotalAbandonments() {
        return getTotalControlAbandonments() + getTotalTreatmentAbandonments();
    }

    public long getTotalControlAbandonments() {
        if (-1L == controlAbandonments) {
            controlAbandonments = sum(SiteMetrics.CONTROL_ABANDONMENT);
        }
        return controlAbandonments;
    }

    public long getTotalTreatmentAbandonments() {
        if (-1L == treatmentAbandonments) {
            treatmentAbandonments = sum(SiteMetrics.TREATMENT_ABANDONMENT);
        }
        return treatmentAbandonments;
    }

    public long[][] getControlAbandonments() {
        long[][] controlABandonments = metrics.get(SiteMetrics.CONTROL_ABANDONMENT);
        return null == controlABandonments ? new long[0][] : controlABandonments;
    }

    public long[][] getTreatmentAbandonments() {
        long[][] controlABandonments = metrics.get(SiteMetrics.TREATMENT_ABANDONMENT);
        return null == controlABandonments ? new long[0][] : controlABandonments;
    }

    public long[][] getControlItems() {
        long[][] controlItems = metrics.get(SiteMetrics.CONTROL_ITEMS);
        return null == controlItems ? new long[0][] : controlItems;
    }

    public long[][] getControlOrders() {
        long[][] controlOrdres = metrics.get(SiteMetrics.CONTROL_ORDERS);
        return null == controlOrdres ? new long[0][] : controlOrdres;
    }

    public double[][] getControlRevenue() {
        long[][] crs = metrics.get(SiteMetrics.CONTROL_REVENUE);
        if (null == crs) {
            return new double[0][];
        }
        List<double[]> result = newArrayListWithCapacity(crs.length);
        for (long[] point : crs) {
            result.add(new double[] { point[0], point[1] / 100D });
        }
        return result.toArray(new double[0][]);
    }

    public double[][] getAbandonedRevenue() {
        return combine(getTreatmentAbandonedRevenue(), getControlAbandonedRevenue());
    }

    public double[][] getControlAbandonedRevenue() {
        long[][] crs = metrics.get(SiteMetrics.CONTROL_ABANDONED_REVENUE);
        if (null == crs) {
            return new double[0][];
        }
        List<double[]> result = newArrayListWithCapacity(crs.length);
        for (long[] point : crs) {
            result.add(new double[] { point[0], point[1] / 100D });
        }
        return result.toArray(new double[0][]);
    }

    public double[][] getTreatmentAbandonedRevenue() {
        long[][] crs = metrics.get(SiteMetrics.TREATMENT_ABANDONED_REVENUE);
        if (null == crs) {
            return new double[0][];
        }
        List<double[]> result = newArrayListWithCapacity(crs.length);
        for (long[] point : crs) {
            result.add(new double[] { point[0], point[1] / 100D });
        }
        return result.toArray(new double[0][]);
    }

    /**
     * @return the end
     */
    public long getEnd() {
        return end;
    }

    public long[][] getGoals() {
        long[][] goals = metrics.get(SiteMetrics.GOALS);
        return null == goals ? new long[0][] : goals;
    }

    public long[][] getImpressions() {
        long[][] impressions = metrics.get(SiteMetrics.DELIVERED);
        return null == impressions ? new long[0][] : impressions;

    }

    /**
     * @return the merchantKey
     */
    public String getMerchantKey() {
        return merchantKey;
    }

    public long[][] getMetric(SiteMetrics metric) {
        return metrics.get(metric);
    }

    /**
     * @return the metrics
     */
    public Map<SiteMetrics, long[][]> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    public double[][] getNormalizedAverageControlOrderValue() {
        return normalize(getAverageControlOrderValue(), getControlNormalizationMultiplier());
    }

    public double[][] getNormalizedAverageTreatmentOrderValue() {
        return normalize(getAverageTreatmentOrderValue(), getTreatmentNormalizationMultiplier());
    }

    public long[][] getNormalizedControlItems() {
        double normalizationFactor = getControlNormalizationMultiplier();
        return normalize(getControlItems(), normalizationFactor);
    }

    public long[][] getNormalizedControlOrders() {
        double normalizationFactor = getControlNormalizationMultiplier();
        return normalize(getControlOrders(), normalizationFactor);
    }

    public double[][] getNormalizedControlRevenue() {
        return normalize(getControlRevenue(), getControlNormalizationMultiplier());
    }

    public long[][] getNormalizedTreatmentItems() {
        double normalizationFactor = getTreatmentNormalizationMultiplier();
        return normalize(getTreatmentItems(), normalizationFactor);
    }

    public long[][] getNormalizedTreatmentOrders() {
        double normalizationFactor = getTreatmentNormalizationMultiplier();
        return normalize(getTreatmentOrders(), normalizationFactor);
    }

    public double[][] getNormalizedTreatmentRevenue() {
        return normalize(getTreatmentRevenue(), getTreatmentNormalizationMultiplier());
    }

    public double[][] getNormalizedControlAbandonedRevenue() {
        return normalize(getControlAbandonedRevenue(), getControlNormalizationMultiplier());
    }

    public double[][] getNormalizedTreatmentAbandonedRevenue() {
        return normalize(getTreatmentAbandonedRevenue(), getTreatmentNormalizationMultiplier());
    }

    public long[][] getNormalizedControlAbandonments() {
        return normalize(getControlAbandonments(), getControlNormalizationMultiplier());
    }

    public long[][] getNormalizedTreatmentAbandonments() {
        return normalize(getTreatmentAbandonments(), getTreatmentNormalizationMultiplier());
    }

    public double getTotalNormalizedControlAbandonedRevenue() {
        return getControlNormalizationMultiplier() * getTotalControlAbandonedRevenue();
    }

    public double getTotalNormalizedTreatmentAbandonedRevenue() {
        return getTreatmentNormalizationMultiplier() * getTotalTreatmentAbandonedRevenue();
    }

    public long getTotalNormalizedControlAbandonments() {
        return (long) (getControlNormalizationMultiplier() * getTotalControlAbandonments());
    }

    public long getTotalNormalizedTreatmentAbandonments() {
        return (long) (getTreatmentNormalizationMultiplier() * getTotalTreatmentAbandonments());
    }

    public long[][] getOrders() {
        long[][] coPoints = getControlOrders();
        long[][] toPoints = getTreatmentOrders();
        long[][] result = combine(coPoints, toPoints);
        return result;
    }

    public double[][] getRevenue() {
        double[][] crPoints = getControlRevenue();
        double[][] trPoints = getTreatmentRevenue();
        return combine(crPoints, trPoints);
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    public double getTotalAverageControlOrderValue() {
        long totalControlOrders = getTotalControlOrders();
        if (0L == totalControlOrders) {
            return 0L;
        }
        return getTotalControlRevenue() / totalControlOrders;
    }

    public double getTotalAverageOrderValue() {
        long totalOrders = getTotalOrders();
        if (0L == totalOrders) {
            return 0L;
        }
        return getTotalRevenue() / totalOrders;
    }

    public double getTotalAverageTreatmentOrderValue() {
        long totalTreatmentOrders = getTotalTreatmentOrders();
        if (0L == totalTreatmentOrders) {
            return 0L;
        }
        return getTotalTreatmentRevenue() / totalTreatmentOrders;
    }

    /**
     * Return the total number of accepts.
     * 
     * @return the number of accepts.
     */
    public long getTotalClicks() {
        if (-1 == clicks) {
            clicks = sum(SiteMetrics.ACCEPTED);
        }
        return clicks;
    }

    public long getTotalItems() {
        return getTotalControlItems() + getTotalTreatmentItems();
    }

    public long getTotalControlItems() {
        if (-1L == controlItems) {
            controlItems = sum(SiteMetrics.CONTROL_ITEMS);
        }
        return controlItems;
    }

    public long getTotalControlOrders() {
        if (-1L == controlOrders) {
            controlOrders = sum(SiteMetrics.CONTROL_ORDERS);
        }
        return controlOrders;
    }

    public double getTotalControlRevenue() {
        if (-1D == controlRevenue) {
            controlRevenue = sum(SiteMetrics.CONTROL_REVENUE) / 100.0D;
        }
        return controlRevenue;
    }

    public double getTotalControlAbandonedRevenue() {
        if (-1D == controlAbandonedRevenue) {
            controlAbandonedRevenue = sum(SiteMetrics.CONTROL_ABANDONED_REVENUE) / 100.0D;
        }
        return controlAbandonedRevenue;
    }

    public double getTotalTreatmentAbandonedRevenue() {
        if (-1D == treatmentAbandonedRevenue) {
            treatmentAbandonedRevenue = sum(SiteMetrics.TREATMENT_ABANDONED_REVENUE) / 100.0D;
        }
        return treatmentAbandonedRevenue;
    }

    public double getTotalConversionPercentage() {
        long totalUniqueVisitors = getTotalUniqueVisitors();
        if (0L == totalUniqueVisitors) {
            return totalUniqueVisitors;
        }
        return 100D * getTotalOrders() / totalUniqueVisitors;
    }

    public long getTotalGoals() {
        if (-1 == goals) {
            goals = sum(SiteMetrics.GOALS);
        }
        return goals;
    }

    /**
     * Return the total campaign deliveries on the site.
     * 
     * @return the total number of campaign deliveries.
     */
    public long getTotalImpressions() {
        if (-1L == impressinos) {
            impressinos = sum(SiteMetrics.DELIVERED);
        }
        return impressinos;
    }

    public long getTotalNormalizedControlItems() {
        return (long) (getControlNormalizationMultiplier() * getTotalControlItems());
    }

    public long getTotalNormalizedControlOrders() {
        return (long) (getControlNormalizationMultiplier() * getTotalControlOrders());
    }

    public double getTotalNormalizedControlRevenue() {
        return getControlNormalizationMultiplier() * getTotalControlRevenue();
    }

    public long getTotalNormalizedTreatmentItems() {
        return (long) (getTreatmentNormalizationMultiplier() * getTotalTreatmentItems());
    }

    public long getTotalNormalizedTreatmentOrders() {
        return (long) (getTreatmentNormalizationMultiplier() * getTotalTreatmentOrders());
    }

    public double getTotalNormalizedTreatmentRevenue() {
        return getTreatmentNormalizationMultiplier() * getTotalTreatmentRevenue();
    }

    public long getTotalOrders() {
        return getTotalTreatmentOrders() + getTotalControlOrders();
    }

    public double getTotalRevenue() {
        return getTotalControlRevenue() + getTotalTreatmentRevenue();
    }

    public double getTotalAbandonedRevenue() {
        return getTotalControlAbandonedRevenue() + getTotalTreatmentAbandonedRevenue();
    }

    public long getTotalTreatmentItems() {
        if (-1L == treatmentItems) {
            treatmentItems = sum(SiteMetrics.TREATMENT_ITEMS);
        }
        return treatmentItems;
    }

    public long getTotalTreatmentOrders() {
        if (-1L == treatmentOrders) {
            treatmentOrders = sum(SiteMetrics.TREATMENT_ORDERS);
        }
        return treatmentOrders;
    }

    public double getTotalTreatmentRevenue() {
        if (-1D == treatmentRevenue) {
            treatmentRevenue = sum(SiteMetrics.TREATMENT_REVENUE) / 100.0D;
        }
        return treatmentRevenue;
    }

    public long getTotalUniqueVisitors() {
        return this.getUniqueControl() + this.getUniqueTreatment();
    }

    public double getTreatmentConversionPercentage() {
        if (0 == getUniqueTreatment()) {
            return 0D;
        }
        return 100D * getTotalTreatmentOrders() / getUniqueTreatment();
    }

    public long[][] getTreatmentItems() {
        long[][] treatmentItems = metrics.get(SiteMetrics.TREATMENT_ITEMS);
        return null == treatmentItems ? new long[0][] : treatmentItems;
    }

    public double getTreatmentNormalizationMultiplier() {
        double normalizationFactor = getNormalizationFactor(false);
        return normalizationFactor == 0D ? 0D : 1D / normalizationFactor;
    }

    public long[][] getTreatmentOrders() {
        long[][] treatmentOrders = metrics.get(SiteMetrics.TREATMENT_ORDERS);
        return null == treatmentOrders ? new long[0][] : treatmentOrders;
    }

    public double[][] getTreatmentRevenue() {
        long[][] trs = metrics.get(SiteMetrics.TREATMENT_REVENUE);
        if (null == trs) {
            return new double[0][];
        }
        List<double[]> result = newArrayListWithCapacity(trs.length);
        for (long[] point : trs) {
            result.add(new double[] { point[0], point[1] / 100D });
        }
        return result.toArray(new double[0][]);
    }

    /**
     * @return the uniqueControl
     */
    public long getUniqueControl() {
        return uniqueControl;
    }

    /**
     * @return the uniqueTreatment
     */
    public long getUniqueTreatment() {
        return uniqueTreatment;
    }

    /**
     * @param isControl
     * @return
     */
    public double getNormalizationFactor(boolean isControl) {
        double groupSize = isControl ? getUniqueControl() : getUniqueTreatment();
        long totalUniqueVisitors = getTotalUniqueVisitors();
        if (0L == totalUniqueVisitors) {
            return 0L;
        }
        return groupSize / totalUniqueVisitors;
    }

    @VisibleForTesting
    long[][] combine(long[][] as, long[][] bs) {
        List<long[]> result = newArrayListWithExpectedSize(max(as.length, bs.length));
        int aIndex = 0, bIndex = 0;

        while (aIndex < as.length && bIndex < bs.length) {
            long[] bDp = bs[bIndex];
            long[] aDp = as[aIndex];
            if (bDp[0] == aDp[0]) {
                result.add(new long[] { bDp[0], bDp[1] + aDp[1] });
                aIndex++;
                bIndex++;
            } else if (bDp[0] < aDp[0]) {
                result.add(new long[] { bDp[0], bDp[1] });
                bIndex++;
            } else {
                result.add(new long[] { aDp[0], aDp[1] });
                aIndex++;
            }
        }
        while (aIndex < as.length) {
            long[] dp = as[aIndex++];
            result.add(new long[] { dp[0], dp[1] });
        }

        while (bIndex < bs.length) {
            long[] dp = bs[bIndex++];
            result.add(new long[] { dp[0], dp[1] });
        }

        return result.toArray(new long[0][]);
    }

    @VisibleForTesting
    double[][] combine(double[][] as, double[][] bs) {
        List<double[]> result = newArrayListWithExpectedSize(max(as.length, bs.length));
        int aIndex = 0, bIndex = 0;

        while (aIndex < as.length && bIndex < bs.length) {
            double[] bDp = bs[bIndex];
            double[] aDp = as[aIndex];
            if (bDp[0] == aDp[0]) {
                result.add(new double[] { bDp[0], bDp[1] + aDp[1] });
                aIndex++;
                bIndex++;
            } else if (bDp[0] < aDp[0]) {
                result.add(new double[] { bDp[0], bDp[1] });
                bIndex++;
            } else {
                result.add(new double[] { aDp[0], aDp[1] });
                aIndex++;
            }
        }
        while (aIndex < as.length) {
            double[] dp = as[aIndex++];
            result.add(new double[] { dp[0], dp[1] });
        }

        while (bIndex < bs.length) {
            double[] dp = bs[bIndex++];
            result.add(new double[] { dp[0], dp[1] });
        }

        return result.toArray(new double[0][]);
    }

    @VisibleForTesting
    double getControlNormalizationMultiplier() {
        double normalizationFactor = getNormalizationFactor(true);

        return 0D == normalizationFactor ? 0D : 1D / normalizationFactor;
    }

    @VisibleForTesting
    static long[][] normalize(long[][] as, double normalizationMultiplier) {
        long[][] normalizedAs = new long[as.length][];
        for (int i = 0; i < as.length; i++) {
            normalizedAs[i] = new long[] { as[i][0], (long) (normalizationMultiplier * as[i][1]) };
        }
        return normalizedAs;
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
    static long sum(long[][] as) {
        long sum = 0;
        for (long[] a : as) {
            sum += a[1];
        }
        return sum;
    }

    @VisibleForTesting
    long sum(SiteMetrics metric) {
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

}
