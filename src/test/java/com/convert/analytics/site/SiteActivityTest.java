/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.site;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class SiteActivityTest {

    @Test
    public void testConstructor() {
        String merchantKey = "mKey";
        long start = 1L;
        long end = 2L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.ACCEPTED, new long[][] { { 100L, 10L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(start, siteActivity.getStart());
        assertEquals(end, siteActivity.getEnd());
        assertEquals(merchantKey, siteActivity.getMerchantKey());
        assertEquals(metrics, siteActivity.getMetrics());
        assertEquals(uniqueControl, siteActivity.getUniqueControl());
        assertEquals(uniqueTreatment, siteActivity.getUniqueTreatment());
    }

    @Test
    public void testGetTotalImpressions_0() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, new long[][] {});
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(0L, siteActivity.getTotalImpressions());
    }

    @Test
    public void testGetTotalImpressions_1() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, new long[][] { { 100L, 1L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(1L, siteActivity.getTotalImpressions());
    }

    @Test
    public void testGetTotalImpressions_2() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(3L, siteActivity.getTotalImpressions()); // 1 + 2
    }

    @Test
    public void testGetTotalImpressions_3() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(6L, siteActivity.getTotalImpressions()); // 1 + 2 + 3
    }

    @Test
    public void testGetTotalClicks_0() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.ACCEPTED, new long[][] {});
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(0L, siteActivity.getTotalClicks());
    }

    @Test
    public void testGetTotalClicks_1() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.ACCEPTED, new long[][] { { 100L, 1L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(1L, siteActivity.getTotalClicks());
    }

    @Test
    public void testGetTotalClicks_2() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 150L, 2L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(3L, siteActivity.getTotalClicks());
    }

    @Test
    public void testGetTotalClicks_3() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(6L, siteActivity.getTotalClicks());
    }

    @Test
    public void testGetAverageAcceptRate_impressions_are_0() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, new long[][] {});
        metrics.put(SiteMetrics.ACCEPTED, new long[][] {});
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(0D, siteActivity.getTotalAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetAverageAccept_no_accepts() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, new long[][] { { 100L, 1L } });
        metrics.put(SiteMetrics.ACCEPTED, new long[][] {});
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(0D, siteActivity.getTotalAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetAverageAcceptRate_impressions_are_half_clicks() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 200L, 2L } });
        metrics.put(SiteMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(50D, siteActivity.getTotalAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetAverageAcceptRate_deliveres_equal_accepts() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        metrics.put(SiteMetrics.ACCEPTED, new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } });
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertEquals(100D, siteActivity.getTotalAverageAcceptPercentage(), 0D);
    }

    @Test
    public void testGetClicks_no_clicks() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[][] {}, siteActivity.getClicks());
    }

    @Test
    public void testGetClicks() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] accepted = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.ACCEPTED, accepted);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(accepted, siteActivity.getClicks());
    }

    @Test
    public void testGetImpressions_no_clicks() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[][] {}, siteActivity.getImpressions());
    }

    @Test
    public void testGetImpressions() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] delivered = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, delivered);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(delivered, siteActivity.getImpressions());
    }

    @Test
    public void testGetGoals_no_clicks() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[][] {}, siteActivity.getGoals());
    }

    @Test
    public void testGetGoals() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] goals = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.GOALS, goals);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(goals, siteActivity.getGoals());
    }

    @Test
    public void testGetControlItems_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[0][], siteActivity.getControlItems());
    }

    @Test
    public void testGetControlItems() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] controlItems = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_ITEMS, controlItems);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(controlItems, siteActivity.getControlItems());
    }

    @Test
    public void testGetTreatmentItems_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[0][], siteActivity.getTreatmentItems());
    }

    @Test
    public void testGetTreatmentItems() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] treatmentItems = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_ITEMS, treatmentItems);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(treatmentItems, siteActivity.getTreatmentItems());
    }

    @Test
    public void testGetTreatmentOrders_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[0][], siteActivity.getTreatmentOrders());
    }

    @Test
    public void testGetTreatmentOrders() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] treatmentOrders = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_ORDERS, treatmentOrders);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(treatmentOrders, siteActivity.getTreatmentOrders());
    }

    @Test
    public void testGetControlOrders_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[0][], siteActivity.getControlOrders());
    }

    @Test
    public void testGetControlOrders() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] controlOrders = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_ORDERS, controlOrders);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(controlOrders, siteActivity.getControlOrders());
    }

    @Test
    public void testGetControlRevenue_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[0][], siteActivity.getControlRevenue());
    }

    @Test
    public void testGetControlRevenue() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100D, 10.10D }, { 200D, 20.20D }, { 300D, 30.30D } },
                siteActivity.getControlRevenue());
    }

    @Test
    public void testGetTreatmentRevenue_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new long[0][], siteActivity.getTreatmentRevenue());
    }

    @Test
    public void testGetTreatmentRevenue() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] treatmentRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100, 10.10 }, { 200, 20.20 }, { 300L, 30.30 } },
                siteActivity.getTreatmentRevenue());
    }

    @Test
    public void testGetRevenue_no_data() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[0][], siteActivity.getRevenue());
    }

    @Test
    public void testGetRevenue_treatment_data_only() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] treatmentRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100, 10.10 }, { 200, 20.20 }, { 300, 30.30 } },
                siteActivity.getRevenue());
    }

    @Test
    public void testGetRevenue_control_data_only() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2010L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100, 10.10 }, { 200, 20.10 }, { 300, 30.30 } },
                siteActivity.getRevenue());
    }

    @Test
    public void testGetRevenue_control_and_treatment_data_1() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2010L }, { 300L, 3030L } };
        long[][] treatmentRevenue = new long[][] { { 100L, 1010L }, { 200L, 2010L }, { 300L, 3030L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100D, 20.20D }, { 200, 40.20 }, { 300, 60.60 } },
                siteActivity.getRevenue());
    }

    @Test
    public void testGetRevenue_control_and_treatment_data_2() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2010L }, { 300L, 3030L } };
        long[][] treatmentRevenue = new long[][] { { 150L, 1010L }, { 250L, 2010L }, { 350L, 3030L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100, 10.10 }, { 150, 10.10 }, { 200, 20.10 }, { 250L, 20.10 },
                { 300, 30.30 }, { 350, 30.30 } }, siteActivity.getRevenue());
    }

    @Test
    public void testGetRevenue_control_and_treatment_data_3() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2010L }, { 300L, 3030L } };
        long[][] treatmentRevenue = new long[][] { { 150L, 1010L }, { 200L, 2010L }, { 350L, 3030L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100, 10.10 }, { 150, 10.10 }, { 200, 40.20 }, { 300, 30.30 },
                { 350, 30.30 } }, siteActivity.getRevenue());
    }

    @Test
    public void testGetRevenue_control_and_treatment_data_4() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2010L }, { 300L, 3030L } };
        long[][] treatmentRevenue = new long[][] { { 200L, 2010L }, { 350L, 3030L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);
        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertArrayEquals(new double[][] { { 100, 10.10 }, { 200, 40.20 }, { 300, 30.30 },
                { 350, 30.30 } }, siteActivity.getRevenue());
    }

    @Test
    public void testGetTotalControlItems_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0L, siteActivity.getTotalControlItems());
    }

    @Test
    public void testGetTotalControlItems() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] controlItems = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_ITEMS, controlItems);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(6L, siteActivity.getTotalControlItems());
    }

    @Test
    public void testGetTotalTreatmentItems_no_items() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0L, siteActivity.getTotalTreatmentItems());
    }

    @Test
    public void testGetTotalTreatmentItems() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] controlItems = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_ITEMS, controlItems);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(6L, siteActivity.getTotalTreatmentItems());
    }

    //
    @Test
    public void testGetTotalControlOrders_no_orders() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0L, siteActivity.getTotalControlOrders());
    }

    @Test
    public void testGetTotalControlOrders() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] controlOrders = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_ORDERS, controlOrders);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(6L, siteActivity.getTotalControlOrders());
    }

    @Test
    public void testGetTotalTreatmentOrders_no_orders() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0L, siteActivity.getTotalTreatmentOrders());
    }

    @Test
    public void testGetTotalTreatmentOrders() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long[][] controlOrders = new long[][] { { 100L, 1L }, { 150L, 2L }, { 200L, 3L } };
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_ORDERS, controlOrders);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(6L, siteActivity.getTotalTreatmentOrders());
    }

    @Test
    public void testGetTotalControlRevenue_no_data() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0D, siteActivity.getTotalControlRevenue(), 0D);
    }

    @Test
    public void testGetTotalControlRevenue() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(60.60D, siteActivity.getTotalControlRevenue(), 0D);
    }

    @Test
    public void testGetTotalTreatmentRevenue_no_data() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0D, siteActivity.getTotalTreatmentRevenue(), 0D);
    }

    @Test
    public void testGetTotalTreatmentRevenue() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] treatmentRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(60.60D, siteActivity.getTotalTreatmentRevenue(), 0D);
    }

    @Test
    public void testGetTotalRevenue_no_data() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0D, siteActivity.getTotalRevenue(), 0D);
    }

    @Test
    public void testGetTotalRevenue_no_control() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] treatmentRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(60.60D, siteActivity.getTotalRevenue(), 0D);
    }

    @Test
    public void testGetTotalRevenue_no_treatment() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(60.60D, siteActivity.getTotalRevenue(), 0D);
    }

    @Test
    public void testGetTotalRevenue() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;
        long[][] controlRevenue = new long[][] { { 100L, 1010L }, { 200L, 2020L }, { 300L, 3030L } };
        long[][] treatmentRevenue = new long[][] { { 150L, 2010L }, { 250L, 4020L }, { 350L, 6030L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_REVENUE, controlRevenue);
        metrics.put(SiteMetrics.TREATMENT_REVENUE, treatmentRevenue);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(181.20D, siteActivity.getTotalRevenue(), 0D);
    }

    public void testGetControlConversionRate_no_data() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0D, siteActivity.getControlConversionPercentage(), 0D);
    }

    @Test
    public void testGetControlConversionRate() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 60L;
        long uniqueTreatment = 200L;
        long[][] controlOrders = new long[][] { { 100L, 10L }, { 200L, 10L }, { 300L, 10L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_ORDERS, controlOrders);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(50D, siteActivity.getControlConversionPercentage(), 0D);
    }

    public void testGetTreatmentConversionRate_no_data() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0D, siteActivity.getTreatmentConversionPercentage(), 0D);
    }

    @Test
    public void testGetTreatmentConversionRate() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 60L;
        long uniqueTreatment = 60L;
        long[][] controlOrders = new long[][] { { 100L, 10L }, { 200L, 10L }, { 300L, 10L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.TREATMENT_ORDERS, controlOrders);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(50D, siteActivity.getTreatmentConversionPercentage(), 0D);
    }

    public void testGetTotalConversionRate_no_visitors() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 0L;
        long uniqueTreatment = 0L;

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0D, siteActivity.getTreatmentConversionPercentage(), 0D);
    }

    public void testGetTotalConversionRate_no_data() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 5L;
        long uniqueTreatment = 20L;

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(0D, siteActivity.getTreatmentConversionPercentage(), 0D);
    }

    @Test
    public void testGetTotalConversionRate() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 60L;
        long uniqueTreatment = 120L;
        long[][] controlOrders = new long[][] { { 100L, 10L }, { 200L, 10L }, { 300L, 10L } };
        long[][] treatmentOrders = new long[][] { { 100L, 20L }, { 200L, 20L }, { 300L, 20L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.CONTROL_ORDERS, controlOrders);
        metrics.put(SiteMetrics.TREATMENT_ORDERS, treatmentOrders);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);

        assertEquals(50D, siteActivity.getTotalConversionPercentage(), 0D);
    }

    @Test
    public void testNormalize() {
        long[][] as = { { 1L, 10L }, { 2L, 20L }, { 4L, 40L } };
        assertArrayEquals(new long[][] { { 1L, 5L }, { 2L, 10L }, { 4L, 20L } }, SiteActivity.normalize(as, 0.5));
    }

    @Test
    public void testGetAverageAcceptPercentage_no_delivery() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 60L;
        long uniqueTreatment = 120L;
        // long[][] deliveried = new long[][] { { 100L, 100L }, { 200L, 200L }, { 300L, 300L } };
        long[][] accepted = new long[][] { { 100L, 20L }, { 200L, 20L }, { 300L, 30L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        // metrics.put(SiteMetrics.DELIVERED, deliveried);
        metrics.put(SiteMetrics.ACCEPTED, accepted);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertArrayEquals(new double[0][], siteActivity.getAverageAcceptPercentage());
    }

    @Test
    public void testGetAverageAcceptPercentage_no_accepts() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 60L;
        long uniqueTreatment = 120L;
        long[][] deliveried = new long[][] { { 100L, 100L }, { 200L, 200L }, { 300L, 300L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, deliveried);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertArrayEquals(new double[][] { { 100D, 0D }, { 200D, 0D }, { 300D, 0D } },
                siteActivity.getAverageAcceptPercentage());
    }

    @Test
    public void testGetAverageAcceptPercentage_1() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 60L;
        long uniqueTreatment = 120L;
        long[][] deliveried = new long[][] { { 100L, 100L }, { 200L, 200L }, { 300L, 300L } };
        long[][] accepted = new long[][] { { 100L, 10L }, { 200L, 20L }, { 300L, 30L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, deliveried);
        metrics.put(SiteMetrics.ACCEPTED, accepted);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertArrayEquals(new double[][] { { 100D, 10D }, { 200D, 10D }, { 300D, 10D } },
                siteActivity.getAverageAcceptPercentage());
    }

    @Test
    public void testGetAverageAcceptPercentage_2() {
        String merchantKey = "mKey";
        long start = 0L;
        long end = System.currentTimeMillis();
        long uniqueControl = 60L;
        long uniqueTreatment = 120L;
        long[][] deliveried = new long[][] { { 100L, 100L }, { 150L, 15L }, { 200L, 200L }, { 300L, 300L } };
        long[][] accepted = new long[][] { { 100L, 10L }, { 200L, 20L }, { 250L, 25L }, { 300L, 30L } };

        Map<SiteMetrics, long[][]> metrics = new HashMap<SiteMetrics, long[][]>();
        metrics.put(SiteMetrics.DELIVERED, deliveried);
        metrics.put(SiteMetrics.ACCEPTED, accepted);

        SiteActivity siteActivity = new SiteActivity(merchantKey, start, end, metrics, uniqueControl, uniqueTreatment);
        assertArrayEquals(new double[][] { { 100D, 10D }, { 150D, 0D }, { 200D, 10D }, { 300D, 10D } },
                siteActivity.getAverageAcceptPercentage());
    }

}
