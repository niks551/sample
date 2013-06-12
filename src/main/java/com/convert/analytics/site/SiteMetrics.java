/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.site;

import java.util.HashSet;
import java.util.Set;

public enum SiteMetrics {

    CONTROL_VISITORS("Unique Control Visitors", "cv"),
    TREATMENT_VISITORS("Unique Experiment Visitors", "tv"),

    DELIVERED("Delivered", "d"),
    ACCEPTED("Accepted", "a"),
    GOALS("Goals", "g"),

    CONTROL_REVENUE("Control Revenue", "cr"),
    TREATMENT_REVENUE("Treatment Revenue", "tr"),

    CONTROL_ORDERS("Control Orders", "co"),
    TREATMENT_ORDERS("Treatment Orders", "to"),

    CONTROL_ITEMS("Control Items", "ci"),
    TREATMENT_ITEMS("Treatment Items", "ti"),

    TREATMENT_ABANDONMENT("Abandoned", "tad"),
    CONTROL_ABANDONMENT("Abandonment", "cad"),

    TREATMENT_ABANDONED_REVENUE("Abandoned Revenue", "tar"),
    CONTROL_ABANDONED_REVENUE("Abandoned Revenue", "car");

    String name;

    private String shortUniqueName;

    SiteMetrics(String name, String shortUniqueName) {
        this.name = name;
        this.shortUniqueName = shortUniqueName;
    }

    public String getName() {
        return name;
    }

    String getShortUniqueName() {
        return shortUniqueName;
    }

    static {
        Set<String> shortNames = new HashSet<String>(SiteMetrics.values().length);
        for (SiteMetrics metrics : SiteMetrics.values()) {
            if (!shortNames.add(metrics.getShortUniqueName())) {
                throw new IllegalStateException(metrics + " doesn't have a unique short name");
            }
        }
    }
}
