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

    @Test
    void test_appendCurrencyCode_parse_ok() {
        iBuilder.appendCurrencyCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("GBP", 0);
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.getIndex()).isEqualTo(3);
        assertThat(parsed.getErrorIndex()).isEqualTo(-1);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isEqualTo(CurrencyUnit.GBP);
    }

    @Test
    void test_appendCurrencyCode_parse_tooShort() {
        iBuilder.appendCurrencyCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("GB", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    @Test
    void test_appendCurrencyCode_parse_empty() {
        iBuilder.appendCurrencyCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendCurrencyNumeric3Code_print() {
        iBuilder.appendCurrencyNumeric3Code();
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEqualTo("826");
        assertThat(test).hasToString("${numeric3Code}");
    }

    @Test
    void test_appendCurrencyNumeric3Code_parse_ok() {
        iBuilder.appendCurrencyNumeric3Code();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("826A", 0);
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.getIndex()).isEqualTo(3);
        assertThat(parsed.getErrorIndex()).isEqualTo(-1);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isEqualTo(CurrencyUnit.GBP);
    }

    @Test
    void test_appendCurrencyNumeric3Code_parse_tooShort() {
        iBuilder.appendCurrencyNumeric3Code();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("82", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    @Test
    void test_appendCurrencyNumeric3Code_parse_badCurrency() {
        iBuilder.appendCurrencyNumeric3Code();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("991A", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    @Test
    void test_appendCurrencyNumeric3Code_parse_empty() {
        iBuilder.appendCurrencyNumeric3Code();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendCurrencyNumericCode_print() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEqualTo("826");
        assertThat(test).hasToString("${numericCode}");
    }

    @Test
    void test_appendCurrencyNumericCode_parse_ok() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("826A", 0);
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.getIndex()).isEqualTo(3);
        assertThat(parsed.getErrorIndex()).isEqualTo(-1);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isEqualTo(CurrencyUnit.GBP);
    }

    @Test
    void test_appendCurrencyNumericCode_parse_ok_padded() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("008A", 0);
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.getIndex()).isEqualTo(3);
        assertThat(parsed.getErrorIndex()).isEqualTo(-1);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency().getCode()).isEqualTo("ALL");
    }

    @Test
    void test_appendCurrencyNumericCode_parse_ok_notPadded1() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("8A", 0);
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.getIndex()).isEqualTo(1);
        assertThat(parsed.getErrorIndex()).isEqualTo(-1);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency().getCode()).isEqualTo("ALL");
    }

    @Test
    void test_appendCurrencyNumericCode_parse_ok_notPadded2() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("51 ", 0);
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.getIndex()).isEqualTo(2);
        assertThat(parsed.getErrorIndex()).isEqualTo(-1);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency().getCode()).isEqualTo("AMD");
    }

    @Test
    void test_appendCurrencyNumericCode_parse_tooShort() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    @Test
    void test_appendCurrencyNumericCode_parse_badCurrency() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("991A", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    @Test
    void test_appendCurrencyNumericCode_parse_empty() {
        iBuilder.appendCurrencyNumericCode();
        var test = iBuilder.toFormatter();
        var parsed = test.parse("", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendCurrencySymbolLocalized_print() {
        iBuilder.appendCurrencySymbolLocalized();
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEqualTo("\u00a3");
        assertThat(test).hasToString("${symbolLocalized}");
    }

    @Test
    void test_appendCurrencySymbolLocalized_parse() {
        iBuilder.appendCurrencySymbolLocalized();
        var test = iBuilder.toFormatter();
        assertThat(test.isParser()).isFalse();
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendLiteral_print() {
        iBuilder.appendLiteral("Hello");
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEqualTo("Hello");
        assertThat(test).hasToString("'Hello'");
    }

    @Test
    void test_appendLiteral_print_empty() {
        iBuilder.appendLiteral("");
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEmpty();
        assertThat(test).hasToString("");
    }

    @Test
    void test_appendLiteral_print_null() {
        iBuilder.appendLiteral((CharSequence) null);
        var test = iBuilder.toFormatter();
        assertThat(test.print(GBP_2_34)).isEmpty();
        assertThat(test).hasToString("");
    }

    @Test
    void test_appendLiteral_parse_ok() {
        iBuilder.appendLiteral("Hello");
        var test = iBuilder.toFormatter();
        var parsed = test.parse("HelloWorld", 0);
        assertThat(parsed.isError()).isFalse();
        assertThat(parsed.getIndex()).isEqualTo(5);
        assertThat(parsed.getErrorIndex()).isEqualTo(-1);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    @Test
    void test_appendLiteral_parse_tooShort() {
        iBuilder.appendLiteral("Hello");
        var test = iBuilder.toFormatter();
        var parsed = test.parse("Hell", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    @Test
    void test_appendLiteral_parse_noMatch() {
        iBuilder.appendLiteral("Hello");
        var test = iBuilder.toFormatter();
        var parsed = test.parse("Helol", 0);
        assertThat(parsed.isError()).isTrue();
        assertThat(parsed.getIndex()).isEqualTo(0);
        assertThat(parsed.getErrorIndex()).isEqualTo(0);
        assertThat(parsed.getAmount()).isNull();
        assertThat(parsed.getCurrency()).isNull();
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_appendAmount() {
        return new Object[][] {
            {GBP_2_34, "2.34"},
            {GBP_23_45, "23.45"},
            {GBP_234_56, "234.56"},
            {GBP_2345_67, "2,345.67"},
            {GBP_1234567_89, "1,234,567.89"},
            {GBP_1234_56789, "1,234.567,89"},
            {GBP_1234567891234_1234567891, "1,234,567,891,234.123,456,789,1"},
            {GBP_MINUS_234_56, "-234.56"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_appendAmount")
    void test_appendAmount(BigMoneyProvider money, String expected) {
        iBuilder.appendAmount();
        var test = iBuilder.toFormatter();
        assertThat(test.print(money)).isEqualTo(expected);
        assertThat(test).hasToString("${amount}");
    }

    @Test
    void test_appendAmount_GBP_1234_56789_France() {
        iBuilder.appendAmount();
        var test = iBuilder.toFormatter(Locale.FRANCE);
        assertThat(test.print(GBP_1234_56789)).isEqualTo("1,234.567,89");
        assertThat(test).hasToString("${amount}");
    }

    @Test
    void test_appendAmount_JPY_2345() {
        iBuilder.appendAmount();
        var test = iBuilder.toFormatter();
        assertThat(test.print(JPY_2345)).isEqualTo("2,345");
        assertThat(test).hasToString("${amount}");
    }

    @Test
    void test_appendAmount_3dp_BHD() {
        iBuilder.appendAmount();
        var test = iBuilder.toFormatter();
        var money = Money.of(CurrencyUnit.of("BHD"), 6345345.735d);
        assertThat(test.print(money)).isEqualTo("6,345,345.735");
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_appendAmountLocalized() {
        return new Object[][] {
            {GBP_2_34, "2" + FR_DECIMAL + "34"},
            {GBP_23_45, "23" + FR_DECIMAL + "45"},
            {GBP_234_56, "234" + FR_DECIMAL + "56"},
            {GBP_2345_67, "2" + FR_GROUP + "345" + FR_DECIMAL + "67"},
            {GBP_1234567_89, "1" + FR_GROUP + "234" + FR_GROUP + "567" + FR_DECIMAL + "89"},
            {GBP_1234_56789, "1" + FR_GROUP + "234" + FR_DECIMAL + "567" + FR_GROUP + "89"},
            {GBP_MINUS_234_56, "-234" + FR_DECIMAL + "56"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_appendAmountLocalized")
    void test_appendAmountLocalized(BigMoneyProvider money, String expected) {
        iBuilder.appendAmountLocalized();
        var test = iBuilder.toFormatter(Locale.FRANCE);
        assertThat(test.print(money)).isEqualTo(expected);
        assertThat(test).hasToString("${amount}");
    }

    @Test
    void test_appendAmountLocalized_GBP_1234_56789_US() {
        iBuilder.appendAmountLocalized();
        var test = iBuilder.toFormatter(Locale.US);
        assertThat(test.print(GBP_1234_56789)).isEqualTo("1,234.567,89");
        assertThat(test).hasToString("${amount}");
    }

    @Test
    void test_appendAmountLocalized_JPY_2345() {
        iBuilder.appendAmountLocalized();
        var test = iBuilder.toFormatter(Locale.FRANCE);
        assertThat(test.print(JPY_2345)).isEqualTo("2" + FR_GROUP + "345");
        assertThat(test).hasToString("${amount}");
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendAmount_MoneyAmountStyle_null() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> iBuilder.appendAmount((MoneyAmountStyle) null));
    }

    public static Object[][] data_appendAmount_MoneyAmountStyle() {
        var noGrouping = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        var group3Comma = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        var group3Space = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        var group3BeforeDp =
                MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        var group3CommaForceDp = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withForcedDecimalPoint(true);
        var group3CommaAbs = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withAbsValue(true);
        var group1Dash = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withGroupingSize(1).withGroupingCharacter('-');
        var group2Dash = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withGroupingSize(2).withGroupingCharacter('-');
        var group4CommaAt = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA
            .withGroupingSize(4).withDecimalPointCharacter('@').withForcedDecimalPoint(true);
        var letters = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withZeroCharacter('A');
        return new Object[][] {
            {noGrouping, "2", "2"},
            {noGrouping, "2123456", "2123456"},
            {noGrouping, "2.34", "2.34"},
            {noGrouping, "23.34", "23.34"},
            {noGrouping, "234.34", "234.34"},
            {noGrouping, "2345.34", "2345.34"},
            {noGrouping, "23456.34", "23456.34"},
            {noGrouping, "234567.34", "234567.34"},
            {noGrouping, "2345678.34", "2345678.34"},
            {noGrouping, "2.345", "2.345"},
            {noGrouping, "2.3456", "2.3456"},
            {noGrouping, "2.34567", "2.34567"},
            {noGrouping, "2.345678", "2.345678"},
            {noGrouping, "2.3456789", "2.3456789"},

            {group3Comma, "2", "2"},
            {group3Comma, "2123456", "2,123,456"},
            {group3Comma, "2.34", "2.34"},
            {group3Comma, "23.34", "23.34"},
            {group3Comma, "234.34", "234.34"},
            {group3Comma, "2345.34", "2,345.34"},
            {group3Comma, "23456.34", "23,456.34"},
            {group3Comma, "234567.34", "234,567.34"},
            {group3Comma, "2345678.34", "2,345,678.34"},
            {group3Comma, "2.345", "2.345"},
            {group3Comma, "2.3456", "2.345,6"},
            {group3Comma, "2.34567", "2.345,67"},
            {group3Comma, "2.345678", "2.345,678"},
            {group3Comma, "2.3456789", "2.345,678,9"},

            {group3Space, "2", "2"},
            {group3Space, "2123456", "2 123 456"},
            {group3Space, "2.34", "2.34"},
            {group3Space, "23.34", "23.34"},
            {group3Space, "234.34", "234.34"},
            {group3Space, "2345.34", "2 345.34"},
            {group3Space, "23456.34", "23 456.34"},
            {group3Space, "234567.34", "234 567.34"},
            {group3Space, "2345678.34", "2 345 678.34"},
            {group3Space, "2.345", "2.345"},
            {group3Space, "2.3456", "2.345 6"},
            {group3Space, "2.34567", "2.345 67"},
            {group3Space, "2.345678", "2.345 678"},
            {group3Space, "2.3456789", "2.345 678 9"},

            {group3BeforeDp, "2", "2"},
            {group3BeforeDp, "2123456", "2,123,456"},
            {group3BeforeDp, "2.34", "2.34"},
            {group3BeforeDp, "23.34", "23.34"},
            {group3BeforeDp, "234.34", "234.34"},
            {group3BeforeDp, "2345.34", "2,345.34"},
            {group3BeforeDp, "23456.34", "23,456.34"},
            {group3BeforeDp, "234567.34", "234,567.34"},
            {group3BeforeDp, "2345678.34", "2,345,678.34"},
            {group3BeforeDp, "2.345", "2.345"},
            {group3BeforeDp, "2.3456", "2.3456"},
            {group3BeforeDp, "2.34567", "2.34567"},
            {group3BeforeDp, "2.345678", "2.345678"},
            {group3BeforeDp, "2.3456789", "2.3456789"},

            {group3CommaForceDp, "2", "2."},
            {group3CommaForceDp, "2123456", "2,123,456."},
            {group3CommaForceDp, "2.34", "2.34"},
            {group3CommaForceDp, "23.34", "23.34"},
            {group3CommaForceDp, "234.34", "234.34"},
            {group3CommaForceDp, "2345.34", "2,345.34"},
            {group3CommaForceDp, "23456.34", "23,456.34"},
            {group3CommaForceDp, "234567.34", "234,567.34"},
            {group3CommaForceDp, "2345678.34", "2,345,678.34"},
            {group3CommaForceDp, "2.345", "2.345"},
            {group3CommaForceDp, "2.3456", "2.345,6"},
            {group3CommaForceDp, "2.34567", "2.345,67"},
            {group3CommaForceDp, "2.345678", "2.345,678"},
            {group3CommaForceDp, "2.3456789", "2.345,678,9"},

            {group3CommaAbs, "2", "2"},
            {group3CommaAbs, "-2", "2"},
            {group3CommaAbs, "2123456", "2,123,456"},
            {group3CommaAbs, "-2123456", "2,123,456"},
            {group3CommaAbs, "-2.34", "2.34"},
            {group3CommaAbs, "-23.34", "23.34"},
            {group3CommaAbs, "-234.34", "234.34"},
            {group3CommaAbs, "-2345.34", "2,345.34"},
            {group3CommaAbs, "-23456.34", "23,456.34"},
            {group3CommaAbs, "-234567.34", "234,567.34"},
            {group3CommaAbs, "-2345678.34", "2,345,678.34"},
            {group3CommaAbs, "-2.345", "2.345"},
            {group3CommaAbs, "-2.3456", "2.345,6"},
            {group3CommaAbs, "-2.34567", "2.345,67"},
            {group3CommaAbs, "-2.345678", "2.345,678"},
            {group3CommaAbs, "-2.3456789", "2.345,678,9"},

            {group1Dash, "2", "2"},
            {group1Dash, "2123456", "2-1-2-3-4-5-6"},
            {group1Dash, "2.34", "2.3-4"},
            {group1Dash, "23.34", "2-3.3-4"},
            {group1Dash, "234.34", "2-3-4.3-4"},
            {group1Dash, "2345.34", "2-3-4-5.3-4"},
            {group1Dash, "23456.34", "2-3-4-5-6.3-4"},
            {group1Dash, "234567.34", "2-3-4-5-6-7.3-4"},
            {group1Dash, "2345678.34", "2-3-4-5-6-7-8.3-4"},
            {group1Dash, "2.345", "2.3-4-5"},
            {group1Dash, "2.3456", "2.3-4-5-6"},
            {group1Dash, "2.34567", "2.3-4-5-6-7"},
            {group1Dash, "2.345678", "2.3-4-5-6-7-8"},
            {group1Dash, "2.3456789", "2.3-4-5-6-7-8-9"},

            {group2Dash, "2", "2"},
            {group2Dash, "2123456", "2-12-34-56"},
            {group2Dash, "2.34", "2.34"},
            {group2Dash, "23.34", "23.34"},
            {group2Dash, "234.34", "2-34.34"},
            {group2Dash, "2345.34", "23-45.34"},
            {group2Dash, "23456.34", "2-34-56.34"},
            {group2Dash, "234567.34", "23-45-67.34"},
            {group2Dash, "2345678.34", "2-34-56-78.34"},
            {group2Dash, "2.345", "2.34-5"},
            {group2Dash, "2.3456", "2.34-56"},
            {group2Dash, "2.34567", "2.34-56-7"},
            {group2Dash, "2.345678", "2.34-56-78"},
            {group2Dash, "2.3456789", "2.34-56-78-9"},

            {group4CommaAt, "2", "2@"},
            {group4CommaAt, "2123456", "212,3456@"},
            {group4CommaAt, "2.34", "2@34"},
            {group4CommaAt, "23.34", "23@34"},
            {group4CommaAt, "234.34", "234@34"},
            {group4CommaAt, "2345.34", "2345@34"},
            {group4CommaAt, "23456.34", "2,3456@34"},
            {group4CommaAt, "234567.34", "23,4567@34"},
            {group4CommaAt, "2345678.34", "234,5678@34"},
            {group4CommaAt, "2.345", "2@345"},
            {group4CommaAt, "2.3456", "2@3456"},
            {group4CommaAt, "2.34567", "2@3456,7"},
            {group4CommaAt, "2.345678", "2@3456,78"},
            {group4CommaAt, "2.3456789", "2@3456,789"},

            {letters, "2", "C"},
            {letters, "2123456", "C,BCD,EFG"},
            {letters, "2.34", "C.DE"},
            {letters, "23.34", "CD.DE"},
            {letters, "234.34", "CDE.DE"},
            {letters, "2345.34", "C,DEF.DE"},
            {letters, "23456.34", "CD,EFG.DE"},
            {letters, "234567.34", "CDE,FGH.DE"},
            {letters, "2345678.34", "C,DEF,GHI.DE"},
            {letters, "2.345", "C.DEF"},
            {letters, "2.3456", "C.DEF,G"},
            {letters, "2.34567", "C.DEF,GH"},
            {letters, "2.345678", "C.DEF,GHI"},
            {letters, "2.3456789", "C.DEF,GHI,J"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_appendAmount_MoneyAmountStyle")
    void test_appendAmount_MoneyAmountStyle_GBP(
            MoneyAmountStyle style,
            String amount,
            String expected) {

        iBuilder.appendAmount(style);
        var test = iBuilder.toFormatter();
        var money = BigMoney.of(GBP, new BigDecimal(amount));
        assertThat(test.print(money)).isEqualTo(expected);
        if (!style.isAbsValue()) {
            assertThat(test.parse(expected, 0).getAmount()).isEqualTo(money.getAmount());
        } else {
            assertThat(test.parse(expected, 0).getAmount()).isEqualTo(money.getAmount().abs());
        }
    }

    @ParameterizedTest
    @MethodSource("data_appendAmount_MoneyAmountStyle")
    void test_appendAmount_MoneyAmountStyle_JPY(
            MoneyAmountStyle style,
            String amount,
            String expected) {

        iBuilder.appendAmount(style);
        var test = iBuilder.toFormatter();
        var money = BigMoney.of(JPY, new BigDecimal(amount));
        assertThat(test.print(money)).isEqualTo(expected);
        if (!style.isAbsValue()) {
            assertThat(test.parse(expected, 0).getAmount()).isEqualTo(money.getAmount());
        } else {
            assertThat(test.parse(expected, 0).getAmount()).isEqualTo(money.getAmount().abs());
        }
    }

    @ParameterizedTest
    @MethodSource("data_appendAmount_MoneyAmountStyle")
    void test_appendAmount_MoneyAmountStyle_BHD(
            MoneyAmountStyle style,
            String amount,
            String expected) {

        iBuilder.appendAmount(style);
        var test = iBuilder.toFormatter();
        var money = BigMoney.of(BHD, new BigDecimal(amount));
        assertThat(test.print(money)).isEqualTo(expected);
        if (!style.isAbsValue()) {
            assertThat(test.parse(expected, 0).getAmount()).isEqualTo(money.getAmount());
        } else {
            assertThat(test.parse(expected, 0).getAmount()).isEqualTo(money.getAmount().abs());
        }
    }

    @Test
    void test_appendAmount_MoneyAmountStyle_JPY_issue49() {
        var money = Money.parse("JPY 12");
        var style = MoneyAmountStyle.LOCALIZED_GROUPING;
        var formatter = new MoneyFormatterBuilder()
            .appendAmount(style)
            .toFormatter()
            .withLocale(Locale.JAPAN);
        assertThat(formatter.print(money)).isEqualTo("12");
    }

    //-----------------------------------------------------------------------
    public static Object[][] data_appendAmountExtendedGrouping() {
        return new Object[][] {
            {GBP_2_34, "2.34"},
            {GBP_23_45, "23.45"},
            {GBP_234_56, "234.56"},
            {GBP_2345_67, "2,345.67"},
            {GBP_1234567_89, "12,34,567.89"},
            {GBP_1234_56789, "1,234.567,89"},
            {GBP_1234567891234_1234567891, "12,34,56,78,91,234.123,45,67,89,1"},
            {GBP_MINUS_234_56, "-234.56"},
        };
    }

    @ParameterizedTest
    @MethodSource("data_appendAmountExtendedGrouping")
    void test_appendAmount_parseExtendedGroupingSize(BigMoneyProvider money, String expected) {
        iBuilder.appendAmount();
        var test = new MoneyFormatterBuilder()
            .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withExtendedGroupingSize(2))
            .toFormatter();
        assertThat(test.print(money)).isEqualTo(expected);
        assertThat(test).hasToString("${amount}");
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendAmount_parseExcessGrouping() {
        var expected = BigMoney.parse("GBP 12123.4567");
        var f = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendLiteral(" ")
            .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA)
            .toFormatter();
        var money = f.parseBigMoney("GBP 12,1,2,3,.,45,6,7");
        assertThat(money).isEqualTo(expected);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_append_MoneyPrinterMoneyParser_printer() {
        MoneyPrinter printer = (context, appendable, money) -> appendable.append("HELLO");
        iBuilder.append(printer, null);
        var test = iBuilder.toFormatter();
        assertThat(test.isPrinter()).isTrue();
        assertThat(test.isParser()).isFalse();
        assertThat(test.print(JPY_2345)).isEqualTo("HELLO");
        assertThat(test.toString()).startsWith("org.joda.money.format.TestMoneyFormatterBuilder$");
    }

    @Test
    void test_append_MoneyPrinterMoneyParser_parser() {
        MoneyParser parser = (context) -> {
            context.setAmount(JPY_2345.getAmount());
            context.setCurrency(JPY_2345.getCurrencyUnit());
        };
        iBuilder.append(null, parser);
        var test = iBuilder.toFormatter();
        assertThat(test.isPrinter()).isFalse();
        assertThat(test.isParser()).isTrue();
        assertThat(test.parseMoney("")).isEqualTo(JPY_2345);
        assertThat(test.toString()).startsWith("org.joda.money.format.TestMoneyFormatterBuilder$");
    }

    @Test
    void test_append_MoneyPrinter_nullMoneyPrinter_nullMoneyParser() {
        iBuilder.append((MoneyPrinter) null, (MoneyParser) null);
        var test = iBuilder.toFormatter();
        assertThat(test.isPrinter()).isFalse();
        assertThat(test.isParser()).isFalse();
        assertThat(test).hasToString("");
    }

    //-----------------------------------------------------------------------
    @Test
    void test_append_MoneyFormatter() {
        var f1 = new MoneyFormatterBuilder().appendAmount().toFormatter();
        var f2 = new MoneyFormatterBuilder().appendCurrencyCode().appendLiteral(" ").append(f1).toFormatter();
        assertThat(f2.print(GBP_2345_67)).isEqualTo("GBP 2,345.67");
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendSigned_PN() {
        var pos = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendLiteral(" ")
            .appendAmount()
            .toFormatter();
        var neg = new MoneyFormatterBuilder()
            .appendLiteral("(")
            .appendCurrencyCode()
            .appendLiteral(" ")
            .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withAbsValue(true))
            .appendLiteral(")")
            .toFormatter();
        var f = new MoneyFormatterBuilder().appendSigned(pos, neg).toFormatter();
        assertThat(f)
            .hasToString("PositiveZeroNegative(${code}' '${amount},${code}' '${amount},'('${code}' '${amount}')')");
        assertThat(f.print(GBP_234_56)).isEqualTo("GBP 234.56");
        assertThat(f.print(Money.zero(GBP))).isEqualTo("GBP 0.00");
        assertThat(f.print(GBP_MINUS_234_56)).isEqualTo("(GBP 234.56)");
        assertThat(f.parseMoney("GBP 234.56")).isEqualTo(GBP_234_56);
        assertThat(f.parseMoney("GBP 0")).isEqualTo(Money.zero(GBP));
        assertThat(f.parseMoney("(GBP 234.56)")).isEqualTo(GBP_MINUS_234_56);
        var context = f.parse("X", 0);
        assertThat(context.getIndex()).isEqualTo(0);
        assertThat(context.getErrorIndex()).isEqualTo(0);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_appendSigned_PZN() {
        var pos = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendLiteral(" ")
            .appendAmount()
            .toFormatter();
        var zro = new MoneyFormatterBuilder()
            .appendCurrencyCode()
            .appendLiteral(" -")
            .toFormatter();
        var neg = new MoneyFormatterBuilder()
            .appendLiteral("(")
            .appendCurrencyCode()
            .appendLiteral(" ")
            .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withAbsValue(true))
            .appendLiteral(")")
            .toFormatter();
        var f = new MoneyFormatterBuilder().appendSigned(pos, zro, neg).toFormatter();
        assertThat(f.print(GBP_234_56)).isEqualTo("GBP 234.56");
        assertThat(f.print(Money.zero(GBP))).isEqualTo("GBP -");
        assertThat(f.print(GBP_MINUS_234_56)).isEqualTo("(GBP 234.56)");
        assertThat(f.parseMoney("GBP 234.56")).isEqualTo(GBP_234_56);
        assertThat(f.parseMoney("GBP -234.56")).isEqualTo(GBP_MINUS_234_56);
        assertThat(f.parseMoney("GBP -")).isEqualTo(Money.zero(GBP));
        assertThat(f.parseMoney("(GBP 234.56)")).isEqualTo(GBP_MINUS_234_56);
        assertThat(f.parseMoney("(GBP -234.56)")).isEqualTo(GBP_MINUS_234_56);
    }

    @Test
    void test_appendSigned_PZN_edgeCases() {
        var pos = new MoneyFormatterBuilder()
            .appendAmount()
            .toFormatter();
        var zro = new MoneyFormatterBuilder()
            .appendAmount()
            .appendLiteral(" (zero)")
            .toFormatter();
        var neg = new MoneyFormatterBuilder()
            .appendLiteral("(")
            .appendAmount(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withAbsValue(true))
            .appendLiteral(")")
            .toFormatter();
        var f = new MoneyFormatterBuilder()
            .appendCurrencyCode().appendLiteral(" ").appendSigned(pos, zro, neg).toFormatter();
        assertThat(f.print(GBP_234_56)).isEqualTo("GBP 234.56");
        assertThat(f.print(BigMoney.zero(GBP).withScale(2))).isEqualTo("GBP 0.00 (zero)");
        assertThat(f.print(GBP_MINUS_234_56)).isEqualTo("GBP (234.56)");
        assertThat(f.parseBigMoney("GBP 234.56")).isEqualTo(GBP_234_56.toBigMoney());
        assertThat(f.parseBigMoney("GBP 0.00 (zero)")).isEqualTo(BigMoney.zero(GBP).withScale(2));
        assertThat(f.parseBigMoney("GBP 1.23 (zero)")).isEqualTo(BigMoney.zero(GBP));
        assertThat(f.parseBigMoney("GBP (234.56)")).isEqualTo(GBP_MINUS_234_56.toBigMoney());
    }

    //-----------------------------------------------------------------------
    @Test
    void test_toFormatter_defaultLocale() {
        var test = iBuilder.toFormatter();
        assertThat(test.getLocale()).isEqualTo(TEST_GB_LOCALE);
    }

    @Test
    void test_toFormatter_Locale() {
        var test = iBuilder.toFormatter(TEST_FR_LOCALE);
        assertThat(test.getLocale()).isEqualTo(TEST_FR_LOCALE);
    }

}
