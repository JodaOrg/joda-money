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
package org.joda.money.format;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

/**
 * Test MoneyParseContext.
 */
class TestMoneyParseContext {

    @Test
    void test_initialState() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThat(test.getAmount()).isNull();
        assertThat(test.getCurrency()).isNull();
        assertThat(test.getIndex()).isEqualTo(0);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        assertThat(test.getText()).hasToString("GBP 123");
        assertThat(test.getTextLength()).isEqualTo(7);
        assertThat(test.isError()).isFalse();
        assertThat(test.isFullyParsed()).isFalse();
        assertThat(test.isComplete()).isFalse();
        var pp = new ParsePosition(0);
        pp.setErrorIndex(-1);
        assertThat(test.toParsePosition()).isEqualTo(pp);
    }

    @Test
    void test_setIndex() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThat(test.getIndex()).isEqualTo(0);
        test.setIndex(2);
        assertThat(test.getIndex()).isEqualTo(2);
    }

    @Test
    void test_setErrorIndex() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        test.setErrorIndex(3);
        assertThat(test.getErrorIndex()).isEqualTo(3);
    }

    @Test
    void test_setError() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThat(test.getIndex()).isEqualTo(0);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        test.setError();
        assertThat(test.getIndex()).isEqualTo(0);
        assertThat(test.getErrorIndex()).isEqualTo(0);
    }

    @Test
    void test_setError_withIndex() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThat(test.getIndex()).isEqualTo(0);
        assertThat(test.getErrorIndex()).isEqualTo(-1);
        test.setIndex(2);
        test.setError();
        assertThat(test.getIndex()).isEqualTo(2);
        assertThat(test.getErrorIndex()).isEqualTo(2);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_isComplete_noCurrency() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setAmount(BigDecimal.TEN);
        assertThat(test.isComplete()).isFalse();
    }

    @Test
    void test_isComplete_noAmount() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setCurrency(CurrencyUnit.GBP);
        assertThat(test.isComplete()).isFalse();
    }

    @Test
    void test_toBigMoney_noCurrency() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setAmount(BigDecimal.TEN);
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> test.toBigMoney());
    }

    @Test
    void test_toBigMoney_noAmount() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setCurrency(CurrencyUnit.GBP);
        assertThatExceptionOfType(MoneyFormatException.class)
            .isThrownBy(() -> test.toBigMoney());
    }

    //-----------------------------------------------------------------------
    @Test
    void test_getTextSubstring_ok() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThat(test.getTextSubstring(0, 2)).isEqualTo("GB");
        assertThat(test.getTextSubstring(5, 7)).isEqualTo("23");
    }

    @Test
    void test_getTextSubstring_beforeStart() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
            .isThrownBy(() -> test.getTextSubstring(-1, 2));
    }

    @Test
    void test_getTextSubstring_afterEnd() {
        var test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
            .isThrownBy(() -> test.getTextSubstring(0, 8));
    }

}
