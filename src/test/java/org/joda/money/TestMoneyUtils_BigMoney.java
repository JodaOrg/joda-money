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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

/**
 * Test MoneyUtils.
 */
class TestMoneyUtils_BigMoney {

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
    @Test
    void test_constructor() throws Exception {
        Constructor<MoneyUtils> con = MoneyUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(con.getModifiers()));
        con.setAccessible(true);
        con.newInstance();
    }

    //-----------------------------------------------------------------------
    // isZero(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isZero() {
        assertTrue(MoneyUtils.isZero(null));
        assertTrue(MoneyUtils.isZero(GBP_0));
        assertFalse(MoneyUtils.isZero(GBP_30));
        assertFalse(MoneyUtils.isZero(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isPositive(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isPositive() {
        assertFalse(MoneyUtils.isPositive(null));
        assertFalse(MoneyUtils.isPositive(GBP_0));
        assertTrue(MoneyUtils.isPositive(GBP_30));
        assertFalse(MoneyUtils.isPositive(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isPositiveOrZero() {
        assertTrue(MoneyUtils.isPositiveOrZero(null));
        assertTrue(MoneyUtils.isPositiveOrZero(GBP_0));
        assertTrue(MoneyUtils.isPositiveOrZero(GBP_30));
        assertFalse(MoneyUtils.isPositiveOrZero(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isNegative(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isNegative() {
        assertFalse(MoneyUtils.isNegative(null));
        assertFalse(MoneyUtils.isNegative(GBP_0));
        assertFalse(MoneyUtils.isNegative(GBP_30));
        assertTrue(MoneyUtils.isNegative(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isNegativeOrZero() {
        assertTrue(MoneyUtils.isNegativeOrZero(null));
        assertTrue(MoneyUtils.isNegativeOrZero(GBP_0));
        assertFalse(MoneyUtils.isNegativeOrZero(GBP_30));
        assertTrue(MoneyUtils.isNegativeOrZero(GBP_M30));
    }

    //-----------------------------------------------------------------------
    // max(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_max1() {
        assertSame(GBP_30, MoneyUtils.max(GBP_20, GBP_30));
    }

    @Test
    void test_max2() {
        assertSame(GBP_30, MoneyUtils.max(GBP_30, GBP_20));
    }

    @Test
    void test_max_differentCurrencies() {
        assertThrows(CurrencyMismatchException.class, () -> {
            MoneyUtils.max(GBP_20, EUR_30);
        });
    }

    @Test
    void test_max_null1() {
        assertSame(GBP_30, MoneyUtils.max((BigMoney) null, GBP_30));
    }

    @Test
    void test_max_null2() {
        assertSame(GBP_20, MoneyUtils.max(GBP_20, (BigMoney) null));
    }

    @Test
    void test_max_nullBoth() {
        assertNull(MoneyUtils.max((BigMoney) null, (BigMoney) null));
    }

    //-----------------------------------------------------------------------
    // min(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_min1() {
        assertSame(GBP_20, MoneyUtils.min(GBP_20, GBP_30));
    }

    @Test
    void test_min2() {
        assertSame(GBP_20, MoneyUtils.min(GBP_30, GBP_20));
    }

    @Test
    void test_min_differentCurrencies() {
        assertThrows(CurrencyMismatchException.class, () -> {
            MoneyUtils.min(GBP_20, EUR_30);
        });
    }

    @Test
    void test_min_null1() {
        assertSame(GBP_30, MoneyUtils.min((BigMoney) null, GBP_30));
    }

    @Test
    void test_min_null2() {
        assertSame(GBP_20, MoneyUtils.min(GBP_20, (BigMoney) null));
    }

    @Test
    void test_min_nullBoth() {
        assertNull(MoneyUtils.min((BigMoney) null, (BigMoney) null));
    }

    //-----------------------------------------------------------------------
    // add(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_add() {
        assertEquals(GBP_50, MoneyUtils.add(GBP_20, GBP_30));
    }

    @Test
    void test_add_differentCurrencies() {
        assertThrows(CurrencyMismatchException.class, () -> {
            MoneyUtils.add(GBP_20, EUR_30);
        });
    }

    @Test
    void test_add_null1() {
        assertSame(GBP_30, MoneyUtils.add((BigMoney) null, GBP_30));
    }

    @Test
    void test_add_null2() {
        assertSame(GBP_20, MoneyUtils.add(GBP_20, (BigMoney) null));
    }

    @Test
    void test_add_nullBoth() {
        assertNull(MoneyUtils.add((BigMoney) null, (BigMoney) null));
    }

    //-----------------------------------------------------------------------
    // subtract(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_subtract() {
        assertEquals(GBP_M10, MoneyUtils.subtract(GBP_20, GBP_30));
    }

    @Test
    void test_subtract_differentCurrencies() {
        assertThrows(CurrencyMismatchException.class, () -> {
            MoneyUtils.subtract(GBP_20, EUR_30);
        });
    }

    @Test
    void test_subtract_null1() {
        assertEquals(GBP_M30, MoneyUtils.subtract((BigMoney) null, GBP_30));
    }

    @Test
    void test_subtract_null2() {
        assertSame(GBP_20, MoneyUtils.subtract(GBP_20, (BigMoney) null));
    }

    @Test
    void test_subtract_nullBoth() {
        assertNull(MoneyUtils.subtract((BigMoney) null, (BigMoney) null));
    }

}
