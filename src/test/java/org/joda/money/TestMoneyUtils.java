/*
 *  Copyright 2009 Stephen Colebourne
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

/**
 * Test MoneyUtils.
 */
@Test
public class TestMoneyUtils {

    private static final CurrencyUnit GBP = CurrencyUnit.of("GBP");
    private static final Money GBP_0 = Money.parse("GBP 0");
    private static final Money GBP_20 = Money.parse("GBP 20");
    private static final Money GBP_30 = Money.parse("GBP 30");
    private static final Money GBP_50 = Money.parse("GBP 50");
    private static final Money GBP_M10 = Money.parse("GBP -10");
    private static final Money GBP_M30 = Money.parse("GBP -30");
    private static final Money EUR_0 = Money.parse("EUR 0");
    private static final Money EUR_30 = Money.parse("EUR 30");

    //-----------------------------------------------------------------------
    // checkNotNull(Object,String)
    //-----------------------------------------------------------------------
    public void test_checkNotNull_notNull() {
        MoneyUtils.checkNotNull(new Object(), "");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_checkNotNull_null() {
        try {
            MoneyUtils.checkNotNull(null, "Hello");
        } catch (NullPointerException ex) {
            assertEquals(ex.getMessage(), "Hello");
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // isZero(Money)
    //-----------------------------------------------------------------------
    public void test_isZero_trueGBP() {
        assertSame(MoneyUtils.isZero(GBP_0), true);
    }

    public void test_isZero_trueEUR() {
        assertSame(MoneyUtils.isZero(EUR_0), true);
    }

    public void test_isZero_false() {
        assertSame(MoneyUtils.isZero(GBP_20), false);
    }

    public void test_isZero_null() {
        assertSame(MoneyUtils.isZero(null), true);
    }

    //-----------------------------------------------------------------------
    // defaultToZero(Money,CurrencyUnit)
    //-----------------------------------------------------------------------
    public void test_defaultToZero_nonNull() {
        assertSame(MoneyUtils.defaultToZero(GBP_20, GBP), GBP_20);
    }

    public void test_defaultToZero_null() {
        assertEquals(MoneyUtils.defaultToZero(null, GBP), GBP_0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_defaultToZero_nullCurrency() {
        MoneyUtils.defaultToZero(GBP_20, (CurrencyUnit) null);
    }

    //-----------------------------------------------------------------------
    // max(Money,Money)
    //-----------------------------------------------------------------------
    public void test_max1() {
        assertSame(MoneyUtils.max(GBP_20, GBP_30), GBP_30);
    }

    public void test_max2() {
        assertSame(MoneyUtils.max(GBP_30, GBP_20), GBP_30);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_max_differentCurrencies() {
        MoneyUtils.max(GBP_20, EUR_30);
    }

    public void test_max_null1() {
        assertSame(MoneyUtils.max(null, GBP_30), GBP_30);
    }

    public void test_max_null2() {
        assertSame(MoneyUtils.max(GBP_20, null), GBP_20);
    }

    public void test_max_nullBoth() {
        assertEquals(MoneyUtils.max(null, null), null);
    }

    //-----------------------------------------------------------------------
    // min(Money,Money)
    //-----------------------------------------------------------------------
    public void test_min1() {
        assertSame(MoneyUtils.min(GBP_20, GBP_30), GBP_20);
    }

    public void test_min2() {
        assertSame(MoneyUtils.min(GBP_30, GBP_20), GBP_20);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_min_differentCurrencies() {
        MoneyUtils.min(GBP_20, EUR_30);
    }

    public void test_min_null1() {
        assertSame(MoneyUtils.min(null, GBP_30), GBP_30);
    }

    public void test_min_null2() {
        assertSame(MoneyUtils.min(GBP_20, null), GBP_20);
    }

    public void test_min_nullBoth() {
        assertEquals(MoneyUtils.min(null, null), null);
    }

    //-----------------------------------------------------------------------
    // add(Money,Money)
    //-----------------------------------------------------------------------
    public void test_add() {
        assertEquals(MoneyUtils.add(GBP_20, GBP_30), GBP_50);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_add_differentCurrencies() {
        MoneyUtils.add(GBP_20, EUR_30);
    }

    public void test_add_null1() {
        assertSame(MoneyUtils.add(null, GBP_30), GBP_30);
    }

    public void test_add_null2() {
        assertSame(MoneyUtils.add(GBP_20, null), GBP_20);
    }

    public void test_add_nullBoth() {
        assertEquals(MoneyUtils.add(null, null), null);
    }

    //-----------------------------------------------------------------------
    // subtract(Money,Money)
    //-----------------------------------------------------------------------
    public void test_subtract() {
        assertEquals(MoneyUtils.subtract(GBP_20, GBP_30), GBP_M10);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_subtract_differentCurrencies() {
        MoneyUtils.subtract(GBP_20, EUR_30);
    }

    public void test_subtract_null1() {
        assertEquals(MoneyUtils.subtract(null, GBP_30), GBP_M30);
    }

    public void test_subtract_null2() {
        assertSame(MoneyUtils.subtract(GBP_20, null), GBP_20);
    }

    public void test_subtract_nullBoth() {
        assertEquals(MoneyUtils.subtract(null, null), null);
    }

}
