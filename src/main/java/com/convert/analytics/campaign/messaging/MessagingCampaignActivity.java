package com.convert.analytics.campaign.messaging;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class MessagingCampaignActivity {

    private final String mKey;

    private final String cKey;

    private final long start;

    private final long end;

    private Map<String, MailingActivity> mailingActivities;

    private transient long totalUniqueTreatmentVisitors = 0L;

    private transient long totalUniqueControlVisitors = 0L;

    /**
     * 
     * @param mKey
     * @param cKey
     * @param start
     * @param end
     * @param mailingActivities
     */
    public MessagingCampaignActivity(String mKey, String cKey, long start, long end,
            Map<String, MailingActivity> mailingActivities) {
        this.mKey = checkNotNull(mKey);
        this.cKey = checkNotNull(cKey);
        this.mailingActivities = mailingActivities;
        this.start = start;
        this.end = end;
    }

    public String getCampaignKey() {
        return cKey;
    }

    public String getMerchantKey() {
        return mKey;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public double getTotalConversionPercentage() {
        long totalUniqueVisitors = getTotalUniqueVisitors();
        if (0L == totalUniqueVisitors) {
            return totalUniqueVisitors;
        }
        return 100D * getTotalOrdersDelivered() / totalUniqueVisitors;
    }

    public double getTotalControlConversionPercentage() {
        return getTotalConversionPercentage(true);
    }

    public double getTotalTreatmentConversionPercentage() {
        return getTotalConversionPercentage(false);
    }

    public double getTotalConversionPercentage(boolean isControl) {
        long totalUniqueVisitors = isControl ? getTotalUniqueControlVisitors() : getTotalUniqueTreatmentVisitors();
        if (0L == totalUniqueVisitors) {
            return totalUniqueVisitors;
        }
        return 100D * getTotalOrdersDelivered() / totalUniqueVisitors;
    }

    public long getTotalTreatmentImpressions() {
        return getTotalImpressions(false);
    }

    public long getTotalControlImpressions() {
        return getTotalImpressions(true);
    }

    public long getTotalImpressions() {
        return getTotalControlImpressions() + getTotalTreatmentImpressions();
    }

    public long getTotalImpressions(boolean isControl) {
        long sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalImpressions(isControl);
        }
        return sum;
    }

    public long getTotalTreatmentClicks() {
        return getTotalClicks(false);
    }

    public long getTotalControlClicks() {
        return getTotalClicks(true);
    }

    public long getTotalClicks(boolean isControl) {
        long sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalClicks(isControl);
        }
        return sum;
    }

    public long getTotalControlGoals() {
        return getTotalGoals(true);
    }

    public long getTotalTreatmentGoals() {
        return getTotalGoals(false);
    }

    public long getTotalGoals() {
        return getTotalTreatmentGoals() + getTotalControlGoals();
    }

    public long getTotalGoals(boolean isControl) {
        long sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalGoals(isControl);
        }
        return sum;
    }

    public double getTotalRevenue() {
        return this.getTotalControlRevenue() + this.getTotalTreatmentRevenue();
    }

    public double getTotalControlRevenue() {
        return getTotalRevenue(true);
    }

    public double getTotalTreatmentRevenue() {
        return getTotalRevenue(false);
    }

    public double getTotalRevenue(boolean isControl) {
        double sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalRevenue(isControl);
        }
        return sum;
    }

    public long getTotalControlOrdersDelivered() {
        return getTotalOrdersDelivered(true);
    }

    public long getTotalTreatmentOrdersDelivered() {
        return getTotalOrdersDelivered(false);
    }

    public long getTotalOrdersAccepted() {
        return getTotalControlOrdersAccepted() + getTotalTreatmentOrdersAccepted();
    }

    public long getTotalOrdersDelivered() {
        return getTotalControlOrdersDelivered() + getTotalTreatmentOrdersDelivered();
    }

    public long getTotalOrdersDelivered(boolean isControl) {
        long sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalOrdersDelivered(isControl);
        }
        return sum;
    }

    public long getTotalControlOrdersAccepted() {
        return getTotalOrdersAccepted(true);
    }

    public long getTotalTreatmentOrdersAccepted() {
        return getTotalOrdersAccepted(false);
    }

    public long getTotalOrdersAccepted(boolean isControl) {
        long sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalOrdersAccepted(isControl);
        }
        return sum;
    }

    //

    public long getTotalControlItems() {
        return getTotalItems(true);
    }

    public long getTotalTreatmentItems() {
        return getTotalItems(false);
    }

    public long getTotalItems() {
        return getTotalControlItems() + getTotalTreatmentItems();
    }

    public long getTotalItems(boolean isControl) {
        long sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalItems(isControl);
        }
        return sum;
    }

    public double getTotalAverageOrderValue() {
        long totalControlOrders = getTotalOrdersDelivered();
        if (0L == totalControlOrders) {
            return 0L;
        }
        return getTotalRevenue() / totalControlOrders;
    }

    public double getTotalControlAverageOrderValue() {
        return getTotalAverageOrderValue(true);
    }

    public double getTotalTreatmentAverageOrderValue() {
        return getTotalAverageOrderValue(false);
    }

    public double getTotalAverageOrderValue(boolean isControl) {
        long totalControlOrders = getTotalOrdersDelivered(isControl);
        if (0L == totalControlOrders) {
            return 0L;
        }
        return getTotalRevenue(isControl) / totalControlOrders;
    }

    public long[][] getTreatmentGoals() {
        return getGoals(false);
    }

    public long[][] getControlGoals() {
        return getGoals(true);
    }

    public long[][] getGoals() {
        List<long[][]> goals = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            goals.add(entry.getValue().getGoals());
        }
        return combineLongs(goals);
    }

    public long[][] getGoals(boolean isControl) {
        List<long[][]> goals = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            goals.add(entry.getValue().getGoals(isControl));

        }
        return combineLongs(goals);
    }

    public long[][] getTreatmentImpressions() {
        return getImpressions(false);
    }

    public long[][] getControlImpressions() {
        return getImpressions(true);
    }

    public long[][] getImpressions() {
        List<long[][]> impressions = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            impressions.add(entry.getValue().getImpressions());
        }
        return combineLongs(impressions);
    }

    public long[][] getImpressions(boolean isControl) {
        List<long[][]> impressions = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            impressions.add(entry.getValue().getImpressions(isControl));
        }
        return combineLongs(impressions);
    }

    public long[][] getControlClicks() {
        return getClicks(true);
    }

    public long[][] getTreatmentClicks() {
        return getClicks(false);
    }

    public long[][] getClicks() {
        List<long[][]> clicks = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            clicks.add(entry.getValue().getClicks());
        }
        return combineLongs(clicks);
    }

    public long[][] getClicks(boolean isControl) {
        List<long[][]> clicks = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            clicks.add(entry.getValue().getClicks(isControl));
        }
        return combineLongs(clicks);
    }

    public long getTotalClicks() {
        long sum = 0;
        for (MailingActivity c2ea : this.mailingActivities.values()) {
            sum += c2ea.getTotalClicks();
        }
        return sum;
    }

    public long getTotalUniqueTreatmentVisitors() {
        if (-1 == totalUniqueTreatmentVisitors) {
            totalUniqueTreatmentVisitors = sumUniqueDeliveries(false);
        }
        return totalUniqueTreatmentVisitors;
    }

    public long getTotalUniqueControlVisitors() {
        return totalUniqueControlVisitors;
    }

    void setTotalUniqueControlVisitors(long totalUniqueControlVisitors) {
        this.totalUniqueControlVisitors = totalUniqueControlVisitors;
    }

    void setTotalUniqueTreatmentVisitors(long totalUniqueTreatmentVisitors) {
        this.totalUniqueTreatmentVisitors = totalUniqueTreatmentVisitors;
    }

    public long getTotalUniqueVisitors() {
        return getTotalUniqueControlVisitors() + getTotalUniqueTreatmentVisitors();
    }

    public double[][] getTreatmentRevenue() {
        return getRevenue(false);
    }

    public double[][] getControlRevenue() {
        return getRevenue(true);
    }

    public double[][] getRevenue() {
        List<double[][]> revenues = new ArrayList<double[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            revenues.add(entry.getValue().getRevenue());
        }
        return combineDoubles(revenues);
    }

    /**
     * @param isControl
     * @return
     */
    public double[][] getRevenue(boolean isControl) {
        List<double[][]> revenues = new ArrayList<double[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            revenues.add(entry.getValue().getRevenue(isControl));
        }
        return combineDoubles(revenues);
    }

    public long[][] getTreatmentOrdersDelivered() {
        return getOrdersDelivered(false);
    }

    public long[][] getControlOrdersDelivered() {
        return getOrdersDelivered(true);
    }

    public long[][] getOrdersDelivered() {
        List<long[][]> orders = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            orders.add(entry.getValue().getOrdersDelivered());
        }
        return combineLongs(orders);
    }

    public long[][] getTreatmentOrdersAccpeted() {
        return getOrdersAccepted(false);
    }

    public long[][] getControlOrdersAccepted() {
        return getOrdersAccepted(true);
    }

    public long[][] getOrdersAccepted() {
        List<long[][]> orders = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            orders.add(entry.getValue().getOrdersAccepted());
        }
        return combineLongs(orders);
    }

    /**
     * @param isControl
     * @return
     */
    public long[][] getOrdersDelivered(boolean isControl) {
        List<long[][]> orders = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            orders.add(entry.getValue().getOrdersDelivered(isControl));

        }
        return combineLongs(orders);
    }

    public long[][] getControlItems() {
        return getItems(true);
    }

    public long[][] getTreatementItems() {
        return getItems(false);
    }

    /**
     * @return
     */
    public long[][] getItems() {
        List<long[][]> orders = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            orders.add(entry.getValue().getItems());

        }
        return combineLongs(orders);
    }

    /**
     * @param isControl
     * @return
     */
    public long[][] getItems(boolean isControl) {
        List<long[][]> orders = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            orders.add(entry.getValue().getItems(isControl));

        }
        return combineLongs(orders);
    }

    /**
     * @param isControl
     * @return
     */
    public long[][] getOrdersAccepted(boolean isControl) {
        List<long[][]> orders = new ArrayList<long[][]>(mailingActivities.size());
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            orders.add(entry.getValue().getOrdersAccepted(isControl));
        }
        return combineLongs(orders);
    }

    public double[][] getAverageControlAcceptPercentage() {
        return getAverageAcceptPercentage(true);
    }

    public double[][] getAverageTreatmentAcceptPercentage() {
        return getAverageAcceptPercentage(false);
    }

    public double getTotalControlAverageAcceptPercentage() {
        return getTotalAverageAcceptPercentage(true);
    }

    public double getTotalTreatmentlAverageAcceptPercentage() {
        return getTotalAverageAcceptPercentage(false);
    }

    public double getTotalAverageAcceptPercentage() {
        long totalDelivered = this.getTotalImpressions();
        if (totalDelivered == 0) {
            return 0;
        }
        long totalAccepted = this.getTotalClicks();

        return 100D * totalAccepted / totalDelivered;
    }

    public double[][] getAverageOrderValue() {
        double[][] rs = getRevenue();
        long[][] os = getOrdersDelivered();

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

    public double[][] getAverageControlOrderValue() {
        return getAverageOrderValue(true);
    }

    public double[][] getAverageTreatmentOrderValue() {
        return getAverageOrderValue(false);
    }

    public double[][] getAverageOrderValue(boolean isControl) {
        double[][] rs = getRevenue(isControl);
        long[][] os = getOrdersDelivered(isControl);

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

    public Map<String, MailingActivity> getMailingActivities() {
        return mailingActivities;
    }

    public MailingActivity getMailingActivity(String eKey) {
        return mailingActivities.get(eKey);
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

    /**
     * Returns the average accept rate as a percentage defined as <code> 100D * accepts/impressions </code>
     * 
     * @return the average accept rate as a percentage.
     */
    public double getTotalAverageAcceptPercentage(boolean isControl) {
        long totalDelivered = this.getTotalImpressions(isControl);
        if (totalDelivered == 0) {
            return 0;
        }
        long totalAccepted = this.getTotalClicks(isControl);

        return 100D * totalAccepted / totalDelivered;
    }

    /**
     * Add an implementation of getAverageAcceptPercentage <br>
     * The average accept percentage calculates the accept percentage as follows: <br>
     * If there are impressions at time t then we return the 100 * accepts / impressions <br>
     * If there were no accepts at time t then we return 0 <br>
     * If there were no impressions at time t then we omit that datapoint. <br>
     * 
     * @param isControl
     * @return a 2 dimensional array of doubles containing a {timestamp, value}
     */
    public double[][] getAverageAcceptPercentage(boolean isControl) {
        long[][] impressions = getImpressions(isControl);
        long[][] clicks = getClicks(isControl);
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

    /**
     * @return
     */
    @VisibleForTesting
    long sumUniqueDeliveries(boolean isControl) {
        long sum = 0L;
        for (Entry<String, MailingActivity> entry : mailingActivities.entrySet()) {
            sum += entry.getValue().sumUniqueDeliveries(isControl);
        }
        return sum;
    }

    /**
     * @param revenues
     * @return
     */
    @VisibleForTesting
    double sumDoubles(Iterable<Double> as) {
        double sum = 0D;
        for (Double a : as) {
            sum += a.doubleValue();
        }
        return sum;
    }

    /**
     * @param revenues
     * @return
     */
    @VisibleForTesting
    long sumLongs(Iterable<Long> as) {
        long sum = 0L;
        for (long a : as) {
            sum += a;
        }
        return sum;
    }

    long[][] combineLongs(Iterable<long[][]> elements) {
        SortedSetMultimap<Long, Long> values = TreeMultimap.create();
        for (long[][] entry : elements) {
            for (int i = 0; i < entry.length; i++) {
                values.put(entry[i][0], entry[i][1]);
            }
        }

        Set<Long> timestamps = values.keySet();
        List<long[]> result = new ArrayList<long[]>(timestamps.size());
        for (long timestamp : timestamps) {
            result.add(new long[] { timestamp, sumLongs(values.get(timestamp)) });
        }
        return result.toArray(new long[0][]);
    }

    double[][] combineDoubles(Iterable<double[][]> elements) {
        SortedSetMultimap<Double, Double> values = TreeMultimap.create();
        for (double[][] entry : elements) {
            for (int i = 0; i < entry.length; i++) {
                values.put(entry[i][0], entry[i][1]);
            }
        }

        Set<Double> timestamps = values.keySet();
        List<double[]> result = new ArrayList<double[]>(timestamps.size());
        for (double timestamp : timestamps) {
            result.add(new double[] { timestamp, sumDoubles(values.get(timestamp)) });
        }
        return result.toArray(new double[0][]);
    }

}
