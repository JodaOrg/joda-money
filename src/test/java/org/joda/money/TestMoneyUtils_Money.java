/*
 *  Copyright 2009-present, Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.money;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test MoneyUtils.
 */
public class TestMoneyUtils_Money {

    private static final Money GBP_0 = Money.parse("GBP 0");
    private static final Money GBP_20 = Money.parse("GBP 20");
    private static final Money GBP_30 = Money.parse("GBP 30");
    private static final Money GBP_50 = Money.parse("GBP 50");
    private static final Money GBP_M10 = Money.parse("GBP -10");
    private static final Money GBP_M30 = Money.parse("GBP -30");
    private static final Money EUR_30 = Money.parse("EUR 30");

    //-----------------------------------------------------------------------
    // checkNotNull(Object,String)
    //-----------------------------------------------------------------------
    @Test
    public void test_checkNotNull_notNull() {
        MoneyUtils.checkNotNull(new Object(), "");
    }

    @Test(expected = NullPointerException.class)
    public void test_checkNotNull_null() {
        try {
            MoneyUtils.checkNotNull(null, "Hello");
        } catch (NullPointerException ex) {
            assertEquals("Hello", ex.getMessage());
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // isZero(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_isZero() {
        assertTrue(MoneyUtils.isZero(null));
        assertTrue(MoneyUtils.isZero(GBP_0));
        assertFalse(MoneyUtils.isZero(GBP_30));
        assertFalse(MoneyUtils.isZero(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isPositive(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_isPositive() {
        assertFalse(MoneyUtils.isPositive(null));
        assertFalse(MoneyUtils.isPositive(GBP_0));
        assertTrue(MoneyUtils.isPositive(GBP_30));
        assertFalse(MoneyUtils.isPositive(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_isPositiveOrZero() {
        assertTrue(MoneyUtils.isPositiveOrZero(null));
        assertTrue(MoneyUtils.isPositiveOrZero(GBP_0));
        assertTrue(MoneyUtils.isPositiveOrZero(GBP_30));
        assertFalse(MoneyUtils.isPositiveOrZero(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isNegative(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_isNegative() {
        assertFalse(MoneyUtils.isNegative(null));
        assertFalse(MoneyUtils.isNegative(GBP_0));
        assertFalse(MoneyUtils.isNegative(GBP_30));
        assertTrue(MoneyUtils.isNegative(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero(Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_isNegativeOrZero() {
        assertTrue(MoneyUtils.isNegativeOrZero(null));
        assertTrue(MoneyUtils.isNegativeOrZero(GBP_0));
        assertFalse(MoneyUtils.isNegativeOrZero(GBP_30));
        assertTrue(MoneyUtils.isNegativeOrZero(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // max(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_max1() {
        assertSame(GBP_30, MoneyUtils.max(GBP_20, GBP_30));
    }

    @Test
    public void test_max2() {
        assertSame(GBP_30, MoneyUtils.max(GBP_30, GBP_20));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_max_differentCurrencies() {
        MoneyUtils.max(GBP_20, EUR_30);
    }

    @Test
    public void test_max_null1() {
        assertSame(GBP_30, MoneyUtils.max((Money) null, GBP_30));
    }

    @Test
    public void test_max_null2() {
        assertSame(GBP_20, MoneyUtils.max(GBP_20, (Money) null));
    }

    @Test
    public void test_max_nullBoth() {
        assertEquals(null, MoneyUtils.max((Money) null, (Money) null));
    }

    //-----------------------------------------------------------------------
    // min(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_min1() {
        assertSame(GBP_20, MoneyUtils.min(GBP_20, GBP_30));
    }

    @Test
    public void test_min2() {
        assertSame(GBP_20, MoneyUtils.min(GBP_30, GBP_20));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_min_differentCurrencies() {
        MoneyUtils.min(GBP_20, EUR_30);
    }

    @Test
    public void test_min_null1() {
        assertSame(GBP_30, MoneyUtils.min((Money) null, GBP_30));
    }

    @Test
    public void test_min_null2() {
        assertSame(GBP_20, MoneyUtils.min(GBP_20, (Money) null));
    }

    @Test
    public void test_min_nullBoth() {
        assertEquals(null, MoneyUtils.min((Money) null, (Money) null));
    }

    //-----------------------------------------------------------------------
    // add(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_add() {
        assertEquals(GBP_50, MoneyUtils.add(GBP_20, GBP_30));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_add_differentCurrencies() {
        MoneyUtils.add(GBP_20, EUR_30);
    }

    @Test
    public void test_add_null1() {
        assertSame(GBP_30, MoneyUtils.add((Money) null, GBP_30));
    }

    @Test
    public void test_add_null2() {
        assertSame(GBP_20, MoneyUtils.add(GBP_20, (Money) null));
    }

    @Test
    public void test_add_nullBoth() {
        assertEquals(null, MoneyUtils.add((Money) null, (Money) null));
    }

    //-----------------------------------------------------------------------
    // subtract(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    public void test_subtract() {
        assertEquals(GBP_M10, MoneyUtils.subtract(GBP_20, GBP_30));
    }

    @Test(expected = CurrencyMismatchException.class)
    public void test_subtract_differentCurrencies() {
        MoneyUtils.subtract(GBP_20, EUR_30);
    }

    @Test
    public void test_subtract_null1() {
        assertEquals(GBP_M30, MoneyUtils.subtract((Money) null, GBP_30));
    }

    @Test
    public void test_subtract_null2() {
        assertSame(GBP_20, MoneyUtils.subtract(GBP_20, (Money) null));
    }

    @Test
    public void test_subtract_nullBoth() {
        assertEquals(null, MoneyUtils.subtract((Money) null, (Money) null));
    }

}
