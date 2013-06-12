/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class CampaignActivityTest {

    @Test
    public void testConstructor() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 1L;
        long end = 2L;
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] { { 100L, 10L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(start, campaignActivity.getStart());
        assertEquals(end, campaignActivity.getEnd());
        assertEquals(mKey, campaignActivity.getMerchantKey());
        assertEquals(metrics, campaignActivity.getMetrics());
        assertEquals(uniqueDeliveries, campaignActivity.getUniqueDeliveries());
    }

    @Test
    public void testGetTotalImpressions_0() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, new long[][] {});
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(0L, campaignActivity.getTotalImpressions());
    }

    @Test
    public void testGetTotalImpressions_1() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, new long[][] { { 100L, 1L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(1L, campaignActivity.getTotalImpressions());
    }

    @Test
    public void testGetTotalImpressions_2() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(3L, campaignActivity.getTotalImpressions()); // 1 + 2
    }

    @Test
    public void testGetTotalImpressions_3() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(6L, campaignActivity.getTotalImpressions()); // 1 + 2 + 3
    }

    @Test
    public void testGetTotalClicks_0() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] {});
        long uniquedeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniquedeliveries);
        assertEquals(0L, campaignActivity.getTotalClicks());
    }

    @Test
    public void testGetTotalClicks_1() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] { { 100L, 1L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(1L, campaignActivity.getTotalClicks());
    }

    @Test
    public void testGetTotalClicks_2() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 150L, 2L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics,
                uniqueDeliveries);
        assertEquals(3L, campaignActivity.getTotalClicks());
    }

    @Test
    public void testGetTotalClicks_3() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics,
                uniqueDeliveries);
        assertEquals(6L, campaignActivity.getTotalClicks());
    }

    @Test
    public void testGetAverageAcceptRate_impressions_are_0() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, new long[][] {});
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] {});
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics,
                uniqueDeliveries);
        assertEquals(0D, campaignActivity.getAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetAverageAccept_no_accepts() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, new long[][] { { 100L, 1L } });
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] {});
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(0D, campaignActivity.getAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetAverageAcceptRate_impressions_are_half_clicks() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 200L, 2L } });
        metrics.put(CampaignMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(50D, campaignActivity.getAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetAverageAcceptRate_deliveres_equal_accepts() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        metrics.put(CampaignMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertEquals(100D, campaignActivity.getAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetClicks_no_clicks() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new long[][] {}, campaignActivity.getClicks());
    }

    @Test
    public void testGetClicks() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] accepted = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ACCEPTED, accepted);
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(accepted, campaignActivity.getClicks());
    }

    @Test
    public void testGetImpressions_no_clicks() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);
        assertArrayEquals(new long[][] {}, campaignActivity.getImpressions());
    }

    @Test
    public void testGetImpressions() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] delivered = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.DELIVERED, delivered);
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(delivered, campaignActivity.getImpressions());
    }

    @Test
    public void testGetGoals_no_clicks() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new long[][] {}, campaignActivity.getGoals());
    }

    @Test
    public void testGetGoals() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] goals = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.GOALS, goals);
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(goals, campaignActivity.getGoals());
    }

    @Test
    public void testGetRevenue_no_data() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new double[0][], campaignActivity.getRevenue());
    }

    @Test
    public void testGetRevenue() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] revenue = new long[][] { { 100L, 1010L }, { 150L, 2020L }, { 200L, 3030L } };
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.REVENUE, revenue);
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new double[][] { { 100D, 10.10D }, { 150D, 20.20D }, { 200D, 30.30D } },
                campaignActivity.getRevenue());
    }

    @Test
    public void testGetOrdersDelivered_no_data() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new double[0][], campaignActivity.getOrdersDelivered());
    }

    @Test
    public void testGetOrdersDelivered() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] ordersDelivered = new long[][] { { 100L, 10L }, { 150L, 20L }, { 200L, 30L } };
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ORDERS_DELIVERED, ordersDelivered);
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new long[][] { { 100L, 10L }, { 150L, 20L }, { 200L, 30L } },
                campaignActivity.getOrdersDelivered());
    }

    @Test
    public void testGetOrdersAccepted_no_data() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new double[0][], campaignActivity.getOrdersAccepted());
    }

    @Test
    public void testGetOrdersAccepted() {
        String mKey = "mKey";
        String cKey = "cKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] ordersAccepted = new long[][] { { 100L, 10L }, { 150L, 20L }, { 200L, 30L } };
        Map<CampaignMetrics, long[][]> metrics = new HashMap<CampaignMetrics, long[][]>();
        metrics.put(CampaignMetrics.ORDERS_ACCEPTED, ordersAccepted);
        long uniqueDeliveries = 5L;
        CampaignActivity campaignActivity = new CampaignActivity(mKey, cKey, start, end, metrics, uniqueDeliveries);

        assertArrayEquals(new long[][] { { 100L, 10L }, { 150L, 20L }, { 200L, 30L } },
                campaignActivity.getOrdersAccepted());
    }

}
