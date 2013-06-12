/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public enum CampaignMetrics {

    DELIVERED("Delivered", "d"),
    UNIQUE_DELIVERED_CONTROL("Unique Delivered Control", "udc"),
    UNIQUE_DELIVERED_TREATMENT("Unique Delivered Experiment", "udt"),

    ACCEPTED("Accepted", "a"),
    UNIQUE_ACCEPTED_CONTROL("Delivered Control", "uac"),
    UNIQUE_ACCEPTED_TREATMENT("Delivered Experiment", "uat"),

    GOALS("Goals", "g"),
    REVENUE("Revenue", "r"),
    ORDERS_DELIVERED("Delivered", "od"),
    ORDERS_ACCEPTED("Orders", "oa"),
    ITEMS("Items", "i");

    String name;

    private String shortUniqueName;

    CampaignMetrics(String name, String shortUniqueName) {
        this.name = name;
        this.shortUniqueName = shortUniqueName;
    }

    public String getName() {
        return name;
    }

    String getShortUniqueName() {
        return shortUniqueName;
    }

    static CampaignMetrics fromShortName(String shortName) {
        for (CampaignMetrics metric : CampaignMetrics.values()) {
            if (metric.getShortUniqueName().equals(shortName)) {
                return metric;
            }
        }
        throw new NoSuchElementException(shortName);
    }

    static {
        Set<String> shortNames = new HashSet<String>(CampaignMetrics.values().length);
        for (CampaignMetrics metrics : CampaignMetrics.values()) {
            if (!shortNames.add(metrics.getShortUniqueName())) {
                throw new IllegalStateException(metrics + " doesn't have a unique short name");
            }
        }
    }
}
