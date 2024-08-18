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

import org.junit.jupiter.api.Test;

/**
 * Test MoneyUtils.
 */
class TestMoneyUtils_Money {

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
    void test_checkNotNull_notNull() {
        MoneyUtils.checkNotNull(new Object(), "");
    }

    @Test
    void test_checkNotNull_null() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> MoneyUtils.checkNotNull(null, "Hello"))
            .withMessage("Hello");
    }

    //-----------------------------------------------------------------------
    // isZero(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_isZero() {
        assertThat(MoneyUtils.isZero(null)).isTrue();
        assertThat(MoneyUtils.isZero(GBP_0)).isTrue();
        assertThat(MoneyUtils.isZero(GBP_30)).isFalse();
        assertThat(MoneyUtils.isZero(GBP_M30)).isFalse();
    }

    //-----------------------------------------------------------------------
    // isPositive(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_isPositive() {
        assertThat(MoneyUtils.isPositive(null)).isFalse();
        assertThat(MoneyUtils.isPositive(GBP_0)).isFalse();
        assertThat(MoneyUtils.isPositive(GBP_30)).isTrue();
        assertThat(MoneyUtils.isPositive(GBP_M30)).isFalse();
    }

    //-----------------------------------------------------------------------
    // isPositiveOrZero(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_isPositiveOrZero() {
        assertThat(MoneyUtils.isPositiveOrZero(null)).isTrue();
        assertThat(MoneyUtils.isPositiveOrZero(GBP_0)).isTrue();
        assertThat(MoneyUtils.isPositiveOrZero(GBP_30)).isTrue();
        assertThat(MoneyUtils.isPositiveOrZero(GBP_M30)).isFalse();
    }

    //-----------------------------------------------------------------------
    // isNegative(Money)
    //-----------------------------------------------------------------------
    @Test
    void test_isNegative() {
        assertThat(MoneyUtils.isNegative(null)).isFalse();
        assertThat(MoneyUtils.isNegative(GBP_0)).isFalse();
        assertThat(MoneyUtils.isNegative(GBP_30)).isFalse();
        assertThat(MoneyUtils.isNegative(GBP_M30)).isTrue();
    }

    //-----------------------------------------------------------------------
    // isNegativeOrZero(Money)
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
        assertThat(MoneyUtils.max((Money) null, GBP_30)).isSameAs(GBP_30);
    }

    @Test
    void test_max_null2() {
        assertThat(MoneyUtils.max(GBP_20, (Money) null)).isSameAs(GBP_20);
    }

    @Test
    void test_max_nullBoth() {
        assertThat(MoneyUtils.max((Money) null, (Money) null)).isNull();
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
        assertThat(MoneyUtils.min((Money) null, GBP_30)).isSameAs(GBP_30);
    }

    @Test
    void test_min_null2() {
        assertThat(MoneyUtils.min(GBP_20, (Money) null)).isSameAs(GBP_20);
    }

    @Test
    void test_min_nullBoth() {
        assertThat(MoneyUtils.min((Money) null, (Money) null)).isNull();
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
        assertThat(MoneyUtils.add((Money) null, GBP_30)).isSameAs(GBP_30);
    }

    @Test
    void test_add_null2() {
        assertThat(MoneyUtils.add(GBP_20, (Money) null)).isSameAs(GBP_20);
    }

    @Test
    void test_add_nullBoth() {
        assertThat(MoneyUtils.add((Money) null, (Money) null)).isNull();
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
        assertThat(MoneyUtils.subtract((Money) null, GBP_30)).isEqualTo(GBP_M30);
    }

    @Test
    void test_subtract_null2() {
        assertThat(MoneyUtils.subtract(GBP_20, (Money) null)).isSameAs(GBP_20);
    }

    @Test
    void test_subtract_nullBoth() {
        assertThat(MoneyUtils.subtract((Money) null, (Money) null)).isNull();
    }

}
