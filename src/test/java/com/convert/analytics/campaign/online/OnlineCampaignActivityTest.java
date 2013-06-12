/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign.online;

import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class OnlineCampaignActivityTest {

    /**
     * Test that even though the engagement activity exists it is for a control not treatment so we return a null.
     */
    @Test
    public void testGetEngagementActivity_1() {
        final String mKey = "mKey";
        final String cKey = "cKey";
        final String eKey = "e1";
        final long start = 0L;
        final long end = System.currentTimeMillis();
        Map<String, C2EActivity> engagementActivities = new HashMap<String, C2EActivity>() {

            {
                HashMap<OnlineCampaignMetric, long[][]> metrics = newHashMap();

                put("e1_1", new C2EActivity(mKey, cKey, eKey, true, start, end,
                        metrics, 0, 0, 0, 0));
            }
        };
        OnlineCampaignActivity onlineCampaignActivity = new OnlineCampaignActivity(mKey, cKey, start, end,
                engagementActivities);
        assertNull(onlineCampaignActivity.getEngagementActivity(eKey, false));
    }

    /**
     * Test that when the engagement key doesn't exist we through an exception.
     */
    @Test
    public void testGetEngagementActivity_2() {
        final String mKey = "mKey";
        final String cKey = "cKey";
        final String eKey = "e1";
        final long start = 0L;
        final long end = System.currentTimeMillis();
        Map<String, C2EActivity> engagementActivities = new HashMap<String, C2EActivity>() {

            {
                HashMap<OnlineCampaignMetric, long[][]> metrics = newHashMap();

                put("e1_1", new C2EActivity(mKey, cKey, eKey, true, start, end,
                        metrics, 0, 0, 0, 0));
            }
        };
        OnlineCampaignActivity onlineCampaignActivity = new OnlineCampaignActivity(mKey, cKey, start, end,
                engagementActivities);
        assertNull(onlineCampaignActivity.getEngagementActivity("doesn't exist", true));
    }

    /**
     * Test that when the engagement activity exists with the correct control field it is returned.
     */
    @Test
    public void testGetEngagementActivity_3() {
        final String mKey = "mKey";
        final String cKey = "cKey";
        final String eKey = "e1";
        final long start = 0L;
        final long end = System.currentTimeMillis();
        final HashMap<OnlineCampaignMetric, long[][]> metrics = newHashMap();
        final C2EActivity c2eActivity = new C2EActivity(mKey, cKey, eKey, true, start, end, metrics, 0, 0, 0, 0);

        Map<String, C2EActivity> engagementActivities = new HashMap<String, C2EActivity>() {

            {

                put("e1_1", c2eActivity);
            }
        };
        OnlineCampaignActivity onlineCampaignActivity = new OnlineCampaignActivity(mKey, cKey, start, end,
                engagementActivities);
        assertEquals(c2eActivity, onlineCampaignActivity.getEngagementActivity(eKey, true));
    }
}
