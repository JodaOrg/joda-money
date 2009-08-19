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
 * Test Money.
 */
@Test
public class TestMoneyUtils {

//    private static final Currency GBP = Currency.getInstance("GBP");
//    private static final Currency EUR = Currency.getInstance("EUR");
//    private static final BigDecimal BIGDEC_2_34 = new BigDecimal("2.34");
//    private static final BigDecimal BIGDEC_M5_78 = new BigDecimal("-5.78");

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
        Money test = Money.parse("GBP 0");
        assertSame(MoneyUtils.isZero(test), true);
    }

    public void test_isZero_trueEUR() {
        Money test = Money.parse("EUR 0");
        assertSame(MoneyUtils.isZero(test), true);
    }

    public void test_isZero_false() {
        Money test = Money.parse("GBP 20");
        assertSame(MoneyUtils.isZero(test), false);
    }

    public void test_isZero_null() {
        assertSame(MoneyUtils.isZero(null), true);
    }

    //-----------------------------------------------------------------------
    // max(Money,Money)
    //-----------------------------------------------------------------------
    public void test_max1() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("GBP 30");
        assertSame(MoneyUtils.max(test1, test2), test2);
    }

    public void test_max2() {
        Money test1 = Money.parse("GBP 40");
        Money test2 = Money.parse("GBP 30");
        assertSame(MoneyUtils.max(test1, test2), test1);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_max_differentCurrencies() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("EUR 30");
        MoneyUtils.max(test1, test2);
    }

    public void test_max_null1() {
        Money test1 = null;
        Money test2 = Money.parse("GBP 30");
        assertSame(MoneyUtils.max(test1, test2), test2);
    }

    public void test_max_null2() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = null;
        assertSame(MoneyUtils.max(test1, test2), test1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_max_nullBoth() {
        MoneyUtils.max(null, null);
    }

    //-----------------------------------------------------------------------
    // min(Money,Money)
    //-----------------------------------------------------------------------
    public void test_min1() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("GBP 30");
        assertSame(MoneyUtils.min(test1, test2), test1);
    }

    public void test_min2() {
        Money test1 = Money.parse("GBP 40");
        Money test2 = Money.parse("GBP 30");
        assertSame(MoneyUtils.min(test1, test2), test2);
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_min_differentCurrencies() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("EUR 30");
        MoneyUtils.min(test1, test2);
    }

    public void test_min_null1() {
        Money test1 = null;
        Money test2 = Money.parse("GBP 30");
        assertSame(MoneyUtils.min(test1, test2), test2);
    }

    public void test_min_null2() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = null;
        assertSame(MoneyUtils.min(test1, test2), test1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_min_nullBoth() {
        MoneyUtils.min(null, null);
    }

    //-----------------------------------------------------------------------
    // add(Money,Money)
    //-----------------------------------------------------------------------
    public void test_add() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("GBP 30");
        assertEquals(MoneyUtils.add(test1, test2), Money.parse("GBP 50"));
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_add_differentCurrencies() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("EUR 30");
        MoneyUtils.add(test1, test2);
    }

    public void test_add_null1() {
        Money test1 = null;
        Money test2 = Money.parse("GBP 30");
        assertSame(MoneyUtils.add(test1, test2), test2);
    }

    public void test_add_null2() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = null;
        assertSame(MoneyUtils.add(test1, test2), test1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_add_nullBoth() {
        MoneyUtils.add(null, null);
    }

    //-----------------------------------------------------------------------
    // subtract(Money,Money)
    //-----------------------------------------------------------------------
    public void test_subtract() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("GBP 30");
        assertEquals(MoneyUtils.subtract(test1, test2), Money.parse("GBP -10"));
    }

    @Test(expectedExceptions = MoneyException.class)
    public void test_subtract_differentCurrencies() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = Money.parse("EUR 30");
        MoneyUtils.subtract(test1, test2);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_subtract_null1() {
        MoneyUtils.subtract(null, Money.parse("GBP 30"));
    }

    public void test_subtract_null2() {
        Money test1 = Money.parse("GBP 20");
        Money test2 = null;
        assertSame(MoneyUtils.subtract(test1, test2), test1);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_subtract_nullBoth() {
        MoneyUtils.subtract(null, null);
    }

}
