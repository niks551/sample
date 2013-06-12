/**
 * (C)2013 Digi-Net Technologies, Inc.
 * 5887 Glenridge Drive
 * Suite 350
 * Atlanta, GA 30328 USA
 * All rights reserved.
 */
package com.convert.analytics;

import com.sun.org.apache.xpath.internal.operations.VariableSafeAbsRef;
import org.apache.commons.math3.distribution.NormalDistribution;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A class to calculate the p-value given the results of 2 variations.
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 */
public class PCalculator {

    /**
     * calculate the z-score.
     * @return the z-score.
     */
    static public double zScore(Variation a, Variation b) {
        return (a.getConversionRate() - b.getConversionRate()) / Math.sqrt(Math.pow(a.getStandardError(), 2) + Math.pow(b.getStandardError(), 2));
    }

    /**
     * calculate the p-value.
     * the p-value represents the confidence we have that A will beat B. and 1 - p-value is the confidence that B will
     * beat A.
     * @param a variation a.
     * @param b variation b.
     * @return the p-value or Double.NaN if the both standard errors are null or the conversion rate of either variations is 0
     *
     */
    static public double pValue(Variation a, Variation b) {
        return new NormalDistribution(0, 1).cumulativeProbability(zScore(a, b));
    }

    /**
     * @param a variation a.
     * @param b variation b.
     * Return the confidence that A will beat B as percentage.
     * @return
     */
    static public double getConfidencePercentage(Variation a , Variation b) {
        return pValue(a, b) * (double) 100;
    }

    /**
     * A variation.
     */
    public static class Variation {

        private int deliveries;
        private int conversions;

        /**
         * Constructor
         * @param deliveries how many deliveries (unique or not)
         * @param conversions the number of conversions (unique or not).
         */
        public Variation(int deliveries, int conversions) {
            this.deliveries = deliveries;
            this.conversions = conversions;
        }

        /**
         * getter
         * @return the number of deliveries
         */
        public int getDeliveries() {
            return deliveries;
        }

        /**
         * getter.
         * @return the conversions.
         */
        public int getConversions() {
            return conversions;
        }

        /**
         * calculate the conversion rate.
         * @return the conversion rate, or NAN
         */
        public double getConversionRate() {
            return (double) this.getConversions() / this.getDeliveries();
        }

        /**
         * Get the standard error.
         * @return
         */
        public double getStandardError() {
            return Math.sqrt(getConversionRate() * (1 - getConversionRate())/ (double) getDeliveries());
        }
    }
}
