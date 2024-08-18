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
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_DE_LOCALE = new Locale("de", "DE", "TEST");
    private static final Locale TEST_LV_LOCALE = new Locale("lv", "LV", "TEST");
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
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
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
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87,654,321.123,456,78");
    }

    @Test
    void test_ASCII_DECIMAL_POINT_GROUP3_SPACE() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
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
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87 654 321.123 456 78");
    }

    @Test
    void test_ASCII_DECIMAL_POINT_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
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
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87654321.12345678");
    }

    @Test
    void test_ASCII_ASCII_DECIMAL_COMMA_GROUP3_DOT() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
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
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87.654.321,123.456.78");
    }

    @Test
    void test_ASCII_DECIMAL_COMMA_GROUP3_SPACE() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
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
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87 654 321,123 456 78");
    }

    @Test
    void test_ASCII_DECIMAL_COMMA_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
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
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87654321,12345678");
    }

    @Test
    void test_LOCALIZED_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING;
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
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87,654,321.123,456,78");
    }

    @Test
    void test_LOCALIZED_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
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
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87654321.12345678");
    }

    @Test
    void test_print_groupBeforeDecimal() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertThat(test.print(MONEY)).isEqualTo("87,654,321.12345678");
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    @Test
    void test_of_Locale_GB() {
        MoneyAmountStyle style = MoneyAmountStyle.of(TEST_GB_LOCALE);
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
        MoneyAmountStyle style = MoneyAmountStyle.of(TEST_DE_LOCALE);
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
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_GB_LOCALE);
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
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_').withDecimalPointCharacter('-');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING.localize(TEST_DE_LOCALE);
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
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING.localize(TEST_LV_LOCALE);
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
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getZeroCharacter()).isNull();
        MoneyAmountStyle test = base.withZeroCharacter('_');
        assertThat(base.getZeroCharacter()).isNull();
        assertThat(test.getZeroCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withZeroCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getZeroCharacter()).isEqualTo((Character) '0');
        MoneyAmountStyle test = base.withZeroCharacter('0');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withZeroCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getZeroCharacter()).isNull();
        MoneyAmountStyle test = base.withZeroCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withPositiveSignCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getPositiveSignCharacter()).isNull();
        MoneyAmountStyle test = base.withPositiveSignCharacter('_');
        assertThat(base.getPositiveSignCharacter()).isNull();
        assertThat(test.getPositiveSignCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withPositiveSignCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getPositiveSignCharacter()).isEqualTo((Character) '+');
        MoneyAmountStyle test = base.withPositiveSignCharacter('+');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withPositiveSignCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getPositiveSignCharacter()).isNull();
        MoneyAmountStyle test = base.withPositiveSignCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withNegativeSignCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getNegativeSignCharacter()).isNull();
        MoneyAmountStyle test = base.withNegativeSignCharacter('_');
        assertThat(base.getNegativeSignCharacter()).isNull();
        assertThat(test.getNegativeSignCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withNegativeSignCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getNegativeSignCharacter()).isEqualTo((Character) '-');
        MoneyAmountStyle test = base.withNegativeSignCharacter('-');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withNegativeSignCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getNegativeSignCharacter()).isNull();
        MoneyAmountStyle test = base.withNegativeSignCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withDecimalPointCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getDecimalPointCharacter()).isNull();
        MoneyAmountStyle test = base.withDecimalPointCharacter('_');
        assertThat(base.getDecimalPointCharacter()).isNull();
        assertThat(test.getDecimalPointCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withDecimalPointCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getDecimalPointCharacter()).isEqualTo((Character) '.');
        MoneyAmountStyle test = base.withDecimalPointCharacter('.');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withDecimalPointCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getDecimalPointCharacter()).isNull();
        MoneyAmountStyle test = base.withDecimalPointCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withGroupingCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingCharacter()).isNull();
        MoneyAmountStyle test = base.withGroupingCharacter('_');
        assertThat(base.getGroupingCharacter()).isNull();
        assertThat(test.getGroupingCharacter()).isEqualTo((Character) '_');
    }

    @Test
    void test_withGroupingCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getGroupingCharacter()).isEqualTo((Character) ',');
        MoneyAmountStyle test = base.withGroupingCharacter(',');
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withGroupingCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingCharacter()).isNull();
        MoneyAmountStyle test = base.withGroupingCharacter(null);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withGroupingStyle() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        MoneyAmountStyle test = base.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertThat(base.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        assertThat(test.getGroupingStyle()).isEqualTo(GroupingStyle.BEFORE_DECIMAL_POINT);
    }

    @Test
    void test_withGroupingStyle_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingStyle()).isEqualTo(GroupingStyle.FULL);
        MoneyAmountStyle test = base.withGroupingStyle(GroupingStyle.FULL);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withGroupingSize() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingSize()).isNull();
        MoneyAmountStyle test = base.withGroupingSize(6);
        assertThat(base.getGroupingSize()).isNull();
        assertThat(test.getGroupingSize()).isEqualTo((Integer) 6);
    }

    @Test
    void test_withGroupingSize_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertThat(base.getGroupingSize()).isEqualTo((Integer) 3);
        MoneyAmountStyle test = base.withGroupingSize(3);
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withGroupingSize_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.getGroupingSize()).isNull();
        MoneyAmountStyle test = base.withGroupingSize(null);
        assertThat(test).isSameAs(base);
    }

    @Test
    void test_withGroupingSize_negative() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> base.withGroupingSize(-1));
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withForcedDecimalPoint() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isForcedDecimalPoint()).isFalse();
        MoneyAmountStyle test = base.withForcedDecimalPoint(true);
        assertThat(base.isForcedDecimalPoint()).isFalse();
        assertThat(test.isForcedDecimalPoint()).isTrue();
    }

    @Test
    void test_withForcedDecimalPoint_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isForcedDecimalPoint()).isFalse();
        MoneyAmountStyle test = base.withForcedDecimalPoint(false);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_withAbsValue() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isAbsValue()).isFalse();
        MoneyAmountStyle test = base.withAbsValue(true);
        assertThat(base.isAbsValue()).isFalse();
        assertThat(test.isAbsValue()).isTrue();
    }

    @Test
    void test_withAbsValue_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(base.isAbsValue()).isFalse();
        MoneyAmountStyle test = base.withAbsValue(false);
        assertThat(test).isSameAs(base);
    }

    //-----------------------------------------------------------------------
    // equals
    //-----------------------------------------------------------------------
    @Test
    void test_equals_same() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(a).isEqualTo(a);
    }

    @Test
    void test_equals_otherType() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(new Object()).isNotEqualTo(a);
    }

    @Test
    void test_equals_null() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(a).isNotEqualTo(null);
    }

    @Test
    void test_equals_equal_zeroChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_zeroChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_positiveChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_positiveChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_negativeChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_negativeChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_decimalPointChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_decimalPointChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_groupingChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_groupingChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_groupingStyle() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_groupingStyle() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.NONE);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_groupingSize() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_groupingSize() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_forcedDecimalPoint_false() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_equal_forcedDecimalPoint_true() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_forcedDecimalPoint() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void test_equals_equal_absValue_false() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true).withAbsValue(false);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true).withAbsValue(false);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_equal_absValue_true() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void test_equals_notEqual_absValue() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        assertThat(b).isNotEqualTo(a);
        assertThat(a).isNotEqualTo(b);
    }

    //-----------------------------------------------------------------------
    @Test
    void test_toString() {
        MoneyAmountStyle test = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertThat(test.toString()).startsWith("MoneyAmountStyle");
    }

}
