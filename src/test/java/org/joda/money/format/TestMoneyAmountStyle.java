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
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test MoneyAmountStyle.
 */
class TestMoneyAmountStyle {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = Locale.of("en", "GB", "TEST");
    private static final Locale TEST_DE_LOCALE = Locale.of("de", "DE", "TEST");
    private static final Locale TEST_LV_LOCALE = Locale.of("lv", "LV", "TEST");
    private static final BigMoney MONEY = BigMoney.of(CurrencyUnit.GBP, new BigDecimal("87654321.12345678"));

    @BeforeEach
    void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
    }

    @AfterEach
    void afterMethod() {
        Locale.setDefault(cCachedLocale);
    }

    //-----------------------------------------------------------------------
    // Constants
    //-----------------------------------------------------------------------
    @Test
    void test_ASCII_DECIMAL_POINT_GROUP3_COMMA() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_ASCII_DECIMAL_POINT_GROUP3_COMMA_print() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87,654,321.123,456,78");
    }

    @Test
    void test_ASCII_DECIMAL_POINT_GROUP3_SPACE() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) ' ');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_ASCII_ASCII_DECIMAL_POINT_GROUP3_SPACE_print() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87 654 321.123 456 78");
    }

    @Test
    void test_ASCII_DECIMAL_POINT_NO_GROUPING() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.NONE);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_ASCII_DECIMAL_POINT_NO_GROUPING_print() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87654321.12345678");
    }

    @Test
    void test_ASCII_ASCII_DECIMAL_COMMA_GROUP3_DOT() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_ASCII_DECIMAL_COMMA_GROUP3_DOT_print() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87.654.321,123.456.78");
    }

    @Test
    void test_ASCII_DECIMAL_COMMA_GROUP3_SPACE() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) ' ');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_ASCII_DECIMAL_COMMA_GROUP3_SPACE_print() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87 654 321,123 456 78");
    }

    @Test
    void test_ASCII_DECIMAL_COMMA_NO_GROUPING() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.NONE);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_ASCII_DECIMAL_COMMA_NO_GROUPING_print() {
        var style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87654321,12345678");
    }

    @Test
    void test_LOCALIZED_GROUPING() {
        var style = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(style.getZeroCharacter()).isNull();
        assertThat(style.getPositiveSignCharacter()).isNull();
        assertThat(style.getNegativeSignCharacter()).isNull();
        assertThat(style.getDecimalPointCharacter()).isNull();
        assertThat(style.getGroupingCharacter()).isNull();
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isNull();
        assertThat(style.getExtendedGroupingSize()).isNull();
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_LOCALIZED_GROUPING_print() {
        var style = MoneyAmountStyle.LOCALIZED_GROUPING;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87,654,321.123,456,78");
    }

    @Test
    void test_LOCALIZED_NO_GROUPING() {
        var style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
        assertThat(style.getZeroCharacter()).isNull();
        assertThat(style.getPositiveSignCharacter()).isNull();
        assertThat(style.getNegativeSignCharacter()).isNull();
        assertThat(style.getDecimalPointCharacter()).isNull();
        assertThat(style.getGroupingCharacter()).isNull();
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.NONE);
        assertThat(style.getGroupingSize()).isNull();
        assertThat(style.getExtendedGroupingSize()).isNull();
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_LOCALIZED_NO_GROUPING_print() {
        var style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87654321.12345678");
    }

    @Test
    void test_print_groupBeforeDecimal() {
        var style = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        var test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87,654,321.12345678");
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    @Test
    void test_of_Locale_GB() {
        var style = MoneyAmountStyle.of(TEST_GB_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_of_Locale_DE() {
        var style = MoneyAmountStyle.of(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    //-----------------------------------------------------------------------
    // localize(Locale)
    //-----------------------------------------------------------------------
    @Test
    void test_localize_GB() {
        var style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_GB_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE() {
        var style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE_fixedZero() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        var style = base.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '_');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE_fixedPositive() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        var style = base.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '_');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE_fixedNegative() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        var style = base.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '_');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE_fixedDecimal() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        var style = base.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) '_');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE_fixedGrouping() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        var style = base.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '_');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE_fixedZeroAndDecimal() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_').withDecimalPointCharacter('-');
        var style = base.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '_');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) '-');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_DE_noGrouping() {
        var style = MoneyAmountStyle.LOCALIZED_NO_GROUPING.localize(TEST_DE_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '.');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.NONE);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    @Test
    void test_localize_LV() {
        var style = MoneyAmountStyle.LOCALIZED_NO_GROUPING.localize(TEST_LV_LOCALE);
        assertThat(style.getZeroCharacter()).isEqualTo((Character) '0');
        assertThat(style.getPositiveSignCharacter()).isEqualTo((Character) '+');
        assertThat(style.getNegativeSignCharacter()).isEqualTo((Character) '-');
        assertThat(style.getDecimalPointCharacter()).isEqualTo((Character) ',');
        assertThat(style.getGroupingCharacter()).isEqualTo((Character) '\u00a0');
        assertThat(style.getGroupingStyle()).isEqualTo(GroupingStyle.NONE);
        assertThat(style.getGroupingSize()).isEqualTo((Integer) 3);
        assertThat(style.getExtendedGroupingSize()).isEqualTo((Integer) 0);
        assertThat(style.isForcedDecimalPoint()).isFalse();
    }

    //-----------------------------------------------------------------------
    // With
    //-----------------------------------------------------------------------
    @Test
    void test_withZeroCharacter() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getZeroCharacter()).isNull();
        var test = base.withZeroCharacter('_');
        assertThat(base.getZeroCharacter()).isNull();
        assertThat(test.getZeroCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withZeroCharacter_same() {
        var base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getZeroCharacter()).isEqualTo((Character) '0');
        var test = base.withZeroCharacter('0');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withZeroCharacter_sameNull() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getZeroCharacter()).isNull();
        var test = base.withZeroCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withPositiveSignCharacter() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getPositiveSignCharacter()).isNull();
        var test = base.withPositiveSignCharacter('_');
        assertThat(base.getPositiveSignCharacter()).isNull();
        assertThat(test.getPositiveSignCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withPositiveSignCharacter_same() {
        var base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getPositiveSignCharacter()).isEqualTo((Character) '+');
        var test = base.withPositiveSignCharacter('+');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withPositiveSignCharacter_sameNull() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getPositiveSignCharacter()).isNull();
        var test = base.withPositiveSignCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withNegativeSignCharacter() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getNegativeSignCharacter()).isNull();
        var test = base.withNegativeSignCharacter('_');
        assertThat(base.getNegativeSignCharacter()).isNull();
        assertThat(test.getNegativeSignCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withNegativeSignCharacter_same() {
        var base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getNegativeSignCharacter()).isEqualTo((Character) '-');
        var test = base.withNegativeSignCharacter('-');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withNegativeSignCharacter_sameNull() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getNegativeSignCharacter()).isNull();
        var test = base.withNegativeSignCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withDecimalPointCharacter() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getDecimalPointCharacter()).isNull();
        var test = base.withDecimalPointCharacter('_');
        assertThat(base.getDecimalPointCharacter()).isNull();
        assertThat(test.getDecimalPointCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withDecimalPointCharacter_same() {
        var base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getDecimalPointCharacter()).isEqualTo((Character) '.');
        var test = base.withDecimalPointCharacter('.');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withDecimalPointCharacter_sameNull() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getDecimalPointCharacter()).isNull();
        var test = base.withDecimalPointCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withGroupingCharacter() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingCharacter()).isNull();
        var test = base.withGroupingCharacter('_');
        assertThat(base.getGroupingCharacter()).isNull();
        assertThat(test.getGroupingCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withGroupingCharacter_same() {
        var base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getGroupingCharacter()).isEqualTo((Character) ',');
        var test = base.withGroupingCharacter(',');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withGroupingCharacter_sameNull() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingCharacter()).isNull();
        var test = base.withGroupingCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withGroupingStyle() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        var test = base.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertThat(base.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(test.getGroupingStyle()).isEqualTo(GroupingStyle.BEFORE_DECIMAL_POINT);
    }

    @Test
    void test_withGroupingStyle_same() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        var test = base.withGroupingStyle(GroupingStyle.FULL);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withGroupingSize() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingSize()).isNull();
        var test = base.withGroupingSize(6);
        assertThat(base.getGroupingSize()).isNull();
        assertThat(test.getGroupingSize()).isEqualTo((Integer) 6);
    }

    @Test
    void test_withGroupingSize_same() {
        var base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getGroupingSize()).isEqualTo((Integer) 3);
        var test = base.withGroupingSize(3);
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withGroupingSize_sameNull() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingSize()).isNull();
        var test = base.withGroupingSize(null);
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withGroupingSize_negative() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> base.withGroupingSize(-1));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withForcedDecimalPoint() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isForcedDecimalPoint()).isFalse();
        var test = base.withForcedDecimalPoint(true);
        assertThat(base.isForcedDecimalPoint()).isFalse();
        assertThat(test.isForcedDecimalPoint()).isTrue();
    }

    @Test
    void test_withForcedDecimalPoint_same() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isForcedDecimalPoint()).isFalse();
        var test = base.withForcedDecimalPoint(false);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withAbsValue() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isAbsValue()).isFalse();
        var test = base.withAbsValue(true);
        assertThat(base.isAbsValue()).isFalse();
        assertThat(test.isAbsValue()).isTrue();
    }

    @Test
    void test_withAbsValue_same() {
        var base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isAbsValue()).isFalse();
        var test = base.withAbsValue(false);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    // equals
    //-----------------------------------------------------------------------
    @Test
    void test_equals_same() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(a).isEqualTo(a);
    }

    @Test
    void test_equals_otherType() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(new Object()).isNotEqualTo(a);
    }

    @Test
    void test_equals_null() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(a).isNotEqualTo(null);
    }

    @Test
    void test_equals_equal_zeroChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_zeroChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_positiveChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_positiveChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_negativeChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_negativeChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_decimalPointChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_decimalPointChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_groupingChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_groupingChar() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_groupingStyle() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_groupingStyle() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.NONE);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_groupingSize() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_groupingSize() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_forcedDecimalPoint_false() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_equal_forcedDecimalPoint_true() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_forcedDecimalPoint() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_absValue_false() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true).withAbsValue(false);
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true).withAbsValue(false);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_equal_absValue_true() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_absValue() {
        var a = MoneyAmountStyle.LOCALIZED_GROUPING;
        var b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_toString() {
        var test = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(test.toString()).startsWith("MoneyAmountStyle");
    }

}
