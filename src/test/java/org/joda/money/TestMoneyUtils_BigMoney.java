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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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
        var con = MoneyUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(con.getModifiers())).isTrue();
        con.setAccessible(true);
        con.newInstance();
    }

    //-----------------------------------------------------------------------
    // isZero(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isZero() {
        assertThat(MoneyUtils.isZero(null)).isTrue();
        assertThat(MoneyUtils.isZero(GBP_0)).isTrue();
        assertThat(MoneyUtils.isZero(GBP_30)).isFalse();
        assertThat(MoneyUtils.isZero(GBP_M30)).isFalse();
    }

    //-----------------------------------------------------------------------
    // isPositive(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isPositive() {
        assertThat(MoneyUtils.isPositive(null)).isFalse();
        assertThat(MoneyUtils.isPositive(GBP_0)).isFalse();
        assertThat(MoneyUtils.isPositive(GBP_30)).isTrue();
        assertThat(MoneyUtils.isPositive(GBP_M30)).isFalse();
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isPositiveOrZero() {
        assertThat(MoneyUtils.isPositiveOrZero(null)).isTrue();
        assertThat(MoneyUtils.isPositiveOrZero(GBP_0)).isTrue();
        assertThat(MoneyUtils.isPositiveOrZero(GBP_30)).isTrue();
        assertThat(MoneyUtils.isPositiveOrZero(GBP_M30)).isFalse();
    }

    //-----------------------------------------------------------------------
    // isNegative(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isNegative() {
        assertThat(MoneyUtils.isNegative(null)).isFalse();
        assertThat(MoneyUtils.isNegative(GBP_0)).isFalse();
        assertThat(MoneyUtils.isNegative(GBP_30)).isFalse();
        assertThat(MoneyUtils.isNegative(GBP_M30)).isTrue();
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero(BigMoney)
    //-----------------------------------------------------------------------
    @Test
    void test_isNegativeOrZero() {
        assertThat(MoneyUtils.isNegativeOrZero(null)).isTrue();
        assertThat(MoneyUtils.isNegativeOrZero(GBP_0)).isTrue();
        assertThat(MoneyUtils.isNegativeOrZero(GBP_30)).isFalse();
        assertThat(MoneyUtils.isNegativeOrZero(GBP_M30)).isTrue();
    }

    //-----------------------------------------------------------------------
    // max(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_max1() {
        assertThat(MoneyUtils.max(GBP_20, GBP_30)).isSameAs(GBP_30);
    }

    @Test
    void test_max2() {
        assertThat(MoneyUtils.max(GBP_30, GBP_20)).isSameAs(GBP_30);
    }

    @Test
    void test_max_differentCurrencies() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> MoneyUtils.max(GBP_20, EUR_30));
    }

    @Test
    void test_max_null1() {
        assertThat(MoneyUtils.max((BigMoney) null, GBP_30)).isSameAs(GBP_30);
    }

    @Test
    void test_max_null2() {
        assertThat(MoneyUtils.max(GBP_20, (BigMoney) null)).isSameAs(GBP_20);
    }

    @Test
    void test_max_nullBoth() {
        assertThat(MoneyUtils.max((BigMoney) null, (BigMoney) null)).isNull();
    }

    //-----------------------------------------------------------------------
    // min(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_min1() {
        assertThat(MoneyUtils.min(GBP_20, GBP_30)).isSameAs(GBP_20);
    }

    @Test
    void test_min2() {
        assertThat(MoneyUtils.min(GBP_30, GBP_20)).isSameAs(GBP_20);
    }

    @Test
    void test_min_differentCurrencies() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> MoneyUtils.min(GBP_20, EUR_30));
    }

    @Test
    void test_min_null1() {
        assertThat(MoneyUtils.min((BigMoney) null, GBP_30)).isSameAs(GBP_30);
    }

    @Test
    void test_min_null2() {
        assertThat(MoneyUtils.min(GBP_20, (BigMoney) null)).isSameAs(GBP_20);
    }

    @Test
    void test_min_nullBoth() {
        assertThat(MoneyUtils.min((BigMoney) null, (BigMoney) null)).isNull();
    }

    //-----------------------------------------------------------------------
    // add(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_add() {
        assertThat(MoneyUtils.add(GBP_20, GBP_30)).isEqualTo(GBP_50);
    }

    @Test
    void test_add_differentCurrencies() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> MoneyUtils.add(GBP_20, EUR_30));
    }

    @Test
    void test_add_null1() {
        assertThat(MoneyUtils.add((BigMoney) null, GBP_30)).isSameAs(GBP_30);
    }

    @Test
    void test_add_null2() {
        assertThat(MoneyUtils.add(GBP_20, (BigMoney) null)).isSameAs(GBP_20);
    }

    @Test
    void test_add_nullBoth() {
        assertThat(MoneyUtils.add((BigMoney) null, (BigMoney) null)).isNull();
    }

    //-----------------------------------------------------------------------
    // subtract(Money,Money)
    //-----------------------------------------------------------------------
    @Test
    void test_subtract() {
        assertThat(MoneyUtils.subtract(GBP_20, GBP_30)).isEqualTo(GBP_M10);
    }

    @Test
    void test_subtract_differentCurrencies() {
        assertThatExceptionOfType(CurrencyMismatchException.class)
            .isThrownBy(() -> MoneyUtils.subtract(GBP_20, EUR_30));
    }

    @Test
    void test_subtract_null1() {
        assertThat(MoneyUtils.subtract((BigMoney) null, GBP_30)).isEqualTo(GBP_M30);
    }

    @Test
    void test_subtract_null2() {
        assertThat(MoneyUtils.subtract(GBP_20, (BigMoney) null)).isSameAs(GBP_20);
    }

    @Test
    void test_subtract_nullBoth() {
        assertThat(MoneyUtils.subtract((BigMoney) null, (BigMoney) null)).isNull();
    }

}
