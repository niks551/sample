/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */
public class CampaignMetricsTest {

    @Test
    public void testUniqueNames() {
        Set<String> shortNames = new HashSet<String>(CampaignMetrics.values().length);
        for (CampaignMetrics metrics : CampaignMetrics.values()) {
            assertTrue(metrics + " doesn't have a unique short name", shortNames.add(metrics.getShortUniqueName()));
        }
    }
}
