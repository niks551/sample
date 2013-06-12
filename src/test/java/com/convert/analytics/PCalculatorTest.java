/**
 * (C)2013 Digi-Net Technologies, Inc.
 * 5887 Glenridge Drive
 * Suite 350
 * Atlanta, GA 30328 USA
 * All rights reserved.
 */
package com.convert.analytics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for PCalculator.
 *
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 */
public class PCalculatorTest {
    @Test
    public void testConversionRate() {
        assertEquals(Double.NaN, new PCalculator.Variation(0, 0).getConversionRate(), 0.0);
        assertEquals(0, new PCalculator.Variation(1, 0).getConversionRate(), 0.0);
        assertEquals(1, new PCalculator.Variation(1, 1).getConversionRate(), 0.0);
        assertEquals(0.5, new PCalculator.Variation(2, 1).getConversionRate(), 0.0);
    }

    @Test
    public void testGetStandardError() {
        assertEquals(Double.NaN, new PCalculator.Variation(0, 0).getStandardError(), 0.0);
        assertEquals(0, new PCalculator.Variation(1, 0).getStandardError(), 0.0);
        assertEquals(0, new PCalculator.Variation(1, 1).getStandardError(), 0.0);
        assertEquals(0.353553390593274, new PCalculator.Variation(2, 1).getStandardError(), 0.0001);
    }

    @Test
    public void testGetZScore() {
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(0, 0), new PCalculator.Variation(0, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(1, 0), new PCalculator.Variation(0, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(0, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(1, 1), new PCalculator.Variation(1, 1)), 0.0);
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.zScore(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(-1.4142135623731, PCalculator.zScore(new PCalculator.Variation(2, 1), new PCalculator.Variation(1, 1)), 0.00001);
        assertEquals(1.52419119543142, PCalculator.zScore(new PCalculator.Variation(2000, 134), new PCalculator.Variation(3000, 169)), 0.00001);

    }

    @Test
    public void testPValue() {
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(0, 0), new PCalculator.Variation(0, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(1, 0), new PCalculator.Variation(0, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(0, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(1, 1), new PCalculator.Variation(1, 1)), 0.0);
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.pValue(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(0.078649603521997, PCalculator.pValue(new PCalculator.Variation(2, 1), new PCalculator.Variation(1, 1)), 0.00001);
        assertEquals(0.936269521624084, PCalculator.pValue(new PCalculator.Variation(2000, 134), new PCalculator.Variation(3000, 169)), 0.00001);

    }

    @Test
    public void testGetConfidencePercentage() {
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(0, 0), new PCalculator.Variation(0, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(1, 0), new PCalculator.Variation(0, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(0, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(1, 1), new PCalculator.Variation(1, 1)), 0.0);
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(Double.NaN, PCalculator.getConfidencePercentage(new PCalculator.Variation(1, 0), new PCalculator.Variation(1, 0)), 0.0);
        assertEquals(7.8649603521997, PCalculator.getConfidencePercentage(new PCalculator.Variation(2, 1), new PCalculator.Variation(1, 1)), 0.00001);
        assertEquals(93.6269521624084, PCalculator.getConfidencePercentage(new PCalculator.Variation(2000, 134), new PCalculator.Variation(3000, 169)), 0.00001);

    }
}
