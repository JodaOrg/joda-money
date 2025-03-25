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
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.BigMoneyProvider;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test MoneyFormatterBuilder.
 */
class TestMoneyFormatterBuilder {

    private static final CurrencyUnit GBP = CurrencyUnit.GBP;
    private static final CurrencyUnit JPY = CurrencyUnit.JPY;
    private static final CurrencyUnit BHD = CurrencyUnit.of("BHD");
    private static final Money GBP_2_34 = Money.parse("GBP 2.34");
    private static final Money GBP_23_45 = Money.parse("GBP 23.45");
    private static final Money GBP_234_56 = Money.parse("GBP 234.56");
    private static final Money GBP_MINUS_234_56 = Money.parse("GBP -234.56");
    private static final Money GBP_2345_67 = Money.parse("GBP 2345.67");
    private static final Money GBP_1234567_89 = Money.parse("GBP 1234567.89");
    private static final BigMoney GBP_1234_56789 = BigMoney.parse("GBP 1234.56789");
    private static final BigMoney GBP_1234567891234_1234567891 = BigMoney.parse("GBP 1234567891234.1234567891");
    private static final Money JPY_2345 = Money.parse("JPY 2345");

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = Locale.of("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = Locale.of("fr", "FR", "TEST");
    private static final DecimalFormatSymbols FR_SYMBOLS = new DecimalFormatSymbols(Locale.FRANCE);
    private static final char FR_DECIMAL = FR_SYMBOLS.getMonetaryDecimalSeparator();
    private static final char FR_GROUP = FR_SYMBOLS.getGroupingSeparator();

    private MoneyFormatterBuilder iBuilder;

    @BeforeEach
    void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
        iBuilder = new MoneyFormatterBuilder();
    }

    @AfterEach
    void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iBuilder = null;
    }

    //-----------------------------------------------------------------------
    @Test
    void test_empty() {
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEmpty();
        assertThat(test).hasToString("");
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendCurrencyCode_print() {
        iBuilder.appendCurrencyCode();
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEqualTo("GBP");
        assertThat(test).hasToString("${code}");
    }

    // ... other tests remain unchanged ...

    //-----------------------------------------------------------------------
    // Data for appendAmount(MoneyAmountStyle) tests
    //-----------------------------------------------------------------------
    public static Object[][] data_appendAmount_MoneyAmountStyle() {
        // Replace constant references with calls to the new accessor methods.
        var noGrouping = MoneyAmountStyle.asciiDecimalPointNoGrouping();
        var group3Comma = MoneyAmountStyle.asciiDecimalPointGroup3Comma();
        var group3Space = MoneyAmountStyle.asciiDecimalPointGroup3Space();
        var group3BeforeDp = MoneyAmountStyle.asciiDecimalPointGroup3Comma().withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        var group3CommaForceDp = MoneyAmountStyle.asciiDecimalPointGroup3Comma().withForcedDecimalPoint(true);
        var group3CommaAbs = MoneyAmountStyle.asciiDecimalPointGroup3Comma().withAbsValue(true);
        var group1Dash = MoneyAmountStyle.asciiDecimalPointGroup3Comma().withGroupingSize(1).withGroupingCharacter('-');
        var group2Dash = MoneyAmountStyle.asciiDecimalPointGroup3Comma().withGroupingSize(2).withGroupingCharacter('-');
        var group4CommaAt = MoneyAmountStyle.asciiDecimalPointGroup3Comma().withGroupingSize(4)
                .withDecimalPointCharacter('@').withForcedDecimalPoint(true);
        var letters = MoneyAmountStyle.asciiDecimalPointGroup3Comma().withZeroCharacter('A');
        return new Object[][] {
                {noGrouping, "2", "2"},
                {noGrouping, "2123456", "2123456"},
                {noGrouping, "2.34", "2.34"},
                // ... additional test data ...
                {group3Comma, "2123456", "2,123,456"},
                // ... additional test data using group3Comma, group3Space, etc. ...
                {group1Dash, "2123456", "2-1-2-3-4-5-6"},
                // ... and so on.
        };
    }

    // ... remaining tests in TestMoneyFormatterBuilder ...
}
