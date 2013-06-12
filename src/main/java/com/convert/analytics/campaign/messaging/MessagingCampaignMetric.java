/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.campaign.messaging;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public enum MessagingCampaignMetric {

    DELIVERED("Delivered", "d"),
    ACCEPTED("Accepted", "a"),

    GOALS("Goals", "g"),

    REVENUE("Revenue", "r"),
    ORDERS_DELIVERED("Orders Delivered", "od"),
    ORDERS_ACCEPTED("Orders Accepted", "oa"),
    ITEMS("Items", "i");

    String name;

    private String shortUniqueName;

    MessagingCampaignMetric(String name, String shortUniqueName) {
        this.name = name;
        this.shortUniqueName = shortUniqueName;
    }

    public String getName() {
        return name;
    }

    String getShortUniqueName() {
        return shortUniqueName;
    }

    static MessagingCampaignMetric fromShortName(String shortName) {
        for (MessagingCampaignMetric metric : MessagingCampaignMetric.values()) {
            if (metric.getShortUniqueName().equals(shortName)) {
                return metric;
            }
        }
        throw new NoSuchElementException(shortName);
    }

    static {
        Set<String> shortNames = new HashSet<String>(MessagingCampaignMetric.values().length);
        for (MessagingCampaignMetric metrics : MessagingCampaignMetric.values()) {
            if (!shortNames.add(metrics.getShortUniqueName())) {
                throw new IllegalStateException(metrics + " doesn't have a unique short name");
            }
        }
    }
}
