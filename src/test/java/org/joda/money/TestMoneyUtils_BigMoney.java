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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.testng.annotations.Test;

/**
 * Test MoneyUtils.
 */
@Test
public class TestMoneyUtils_BigMoney {

    private static final BigMoney GBP_0 = BigMoney.parse("GBP 0");
    private static final BigMoney GBP_20 = BigMoney.parse("GBP 20");
    private static final BigMoney GBP_30 = BigMoney.parse("GBP 30");
    private static final BigMoney GBP_50 = BigMoney.parse("GBP 50");
    private static final BigMoney GBP_M10 = BigMoney.parse("GBP -10");
    private static final BigMoney GBP_M30 = BigMoney.parse("GBP -30");
    private static final BigMoney EUR_30 = BigMoney.parse("EUR 30");

    //-----------------------------------------------------------------------
    // constructor
    //-----------------------------------------------------------------------
    public void test_constructor() throws Exception {
        Constructor<MoneyUtils> con = MoneyUtils.class.getDeclaredConstructor();
        assertEquals(Modifier.isPrivate(con.getModifiers()), true);
        con.setAccessible(true);
        con.newInstance();
    }

    //-----------------------------------------------------------------------
    // isZero(BigMoney)
    //-----------------------------------------------------------------------
    public void test_isZero() {
        assertSame(MoneyUtils.isZero(null), true);
        assertSame(MoneyUtils.isZero(GBP_0), true);
        assertSame(MoneyUtils.isZero(GBP_30), false);
        assertSame(MoneyUtils.isZero(GBP_M30), false);
    }

    //-----------------------------------------------------------------------
    // isPositive(BigMoney)
    //-----------------------------------------------------------------------
    public void test_isPositive() {
        assertSame(MoneyUtils.isPositive(null), false);
        assertSame(MoneyUtils.isPositive(GBP_0), false);
        assertSame(MoneyUtils.isPositive(GBP_30), true);
        assertSame(MoneyUtils.isPositive(GBP_M30), false);
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero(BigMoney)
    //-----------------------------------------------------------------------
    public void test_isPositiveOrZero() {
        assertSame(MoneyUtils.isPositiveOrZero(null), true);
        assertSame(MoneyUtils.isPositiveOrZero(GBP_0), true);
        assertSame(MoneyUtils.isPositiveOrZero(GBP_30), true);
        assertSame(MoneyUtils.isPositiveOrZero(GBP_M30), false);
    }

    //-----------------------------------------------------------------------
    // isNegative(BigMoney)
    //-----------------------------------------------------------------------
    public void test_isNegative() {
        assertSame(MoneyUtils.isNegative(null), false);
        assertSame(MoneyUtils.isNegative(GBP_0), false);
        assertSame(MoneyUtils.isNegative(GBP_30), false);
        assertSame(MoneyUtils.isNegative(GBP_M30), true);
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero(BigMoney)
    //-----------------------------------------------------------------------
    public void test_isNegativeOrZero() {
        assertSame(MoneyUtils.isNegativeOrZero(null), true);
        assertSame(MoneyUtils.isNegativeOrZero(GBP_0), true);
        assertSame(MoneyUtils.isNegativeOrZero(GBP_30), false);
        assertSame(MoneyUtils.isNegativeOrZero(GBP_M30), true);
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

    @Test(expectedExceptions = CurrencyMismatchException.class)
    public void test_max_differentCurrencies() {
        MoneyUtils.max(GBP_20, EUR_30);
    }

    public void test_max_null1() {
        assertSame(MoneyUtils.max((BigMoney) null, GBP_30), GBP_30);
    }

    public void test_max_null2() {
        assertSame(MoneyUtils.max(GBP_20, (BigMoney) null), GBP_20);
    }

    public void test_max_nullBoth() {
        assertEquals(MoneyUtils.max((BigMoney) null, (BigMoney) null), null);
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

    @Test(expectedExceptions = CurrencyMismatchException.class)
    public void test_min_differentCurrencies() {
        MoneyUtils.min(GBP_20, EUR_30);
    }

    public void test_min_null1() {
        assertSame(MoneyUtils.min((BigMoney) null, GBP_30), GBP_30);
    }

    public void test_min_null2() {
        assertSame(MoneyUtils.min(GBP_20, (BigMoney) null), GBP_20);
    }

    public void test_min_nullBoth() {
        assertEquals(MoneyUtils.min((BigMoney) null, (BigMoney) null), null);
    }

    //-----------------------------------------------------------------------
    // add(Money,Money)
    //-----------------------------------------------------------------------
    public void test_add() {
        assertEquals(MoneyUtils.add(GBP_20, GBP_30), GBP_50);
    }

    @Test(expectedExceptions = CurrencyMismatchException.class)
    public void test_add_differentCurrencies() {
        MoneyUtils.add(GBP_20, EUR_30);
    }

    public void test_add_null1() {
        assertSame(MoneyUtils.add((BigMoney) null, GBP_30), GBP_30);
    }

    public void test_add_null2() {
        assertSame(MoneyUtils.add(GBP_20, (BigMoney) null), GBP_20);
    }

    public void test_add_nullBoth() {
        assertEquals(MoneyUtils.add((BigMoney) null, (BigMoney) null), null);
    }

    //-----------------------------------------------------------------------
    // subtract(Money,Money)
    //-----------------------------------------------------------------------
    public void test_subtract() {
        assertEquals(MoneyUtils.subtract(GBP_20, GBP_30), GBP_M10);
    }

    @Test(expectedExceptions = CurrencyMismatchException.class)
    public void test_subtract_differentCurrencies() {
        MoneyUtils.subtract(GBP_20, EUR_30);
    }

    public void test_subtract_null1() {
        assertEquals(MoneyUtils.subtract((BigMoney) null, GBP_30), GBP_M30);
    }

    public void test_subtract_null2() {
        assertSame(MoneyUtils.subtract(GBP_20, (BigMoney) null), GBP_20);
    }

    public void test_subtract_nullBoth() {
        assertEquals(MoneyUtils.subtract((BigMoney) null, (BigMoney) null), null);
    }

}
