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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test MoneyAmountStyle.
 */
@Test
public class TestMoneyAmountStyle {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_DE_LOCALE = new Locale("de", "DE", "TEST");
    private static final BigMoney MONEY = BigMoney.of(CurrencyUnit.GBP, new BigDecimal("87654321.12345678"));

    @BeforeMethod
    public void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
    }

    @AfterMethod
    public void afterMethod() {
        Locale.setDefault(cCachedLocale);
    }

    //-----------------------------------------------------------------------
    // Constants
    //-----------------------------------------------------------------------
    public void test_ASCII_DECIMAL_POINT_GROUP3_COMMA() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) '.');
        assertEquals(style.getGroupingCharacter(), (Character) ',');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_ASCII_DECIMAL_POINT_GROUP3_COMMA_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87,654,321.123,456,78");
    }

    public void test_ASCII_DECIMAL_POINT_GROUP3_SPACE() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) '.');
        assertEquals(style.getGroupingCharacter(), (Character) ' ');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_ASCII_ASCII_DECIMAL_POINT_GROUP3_SPACE_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87 654 321.123 456 78");
    }

    public void test_ASCII_DECIMAL_POINT_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) '.');
        assertEquals(style.getGroupingCharacter(), (Character) ',');
        assertEquals(style.getGroupingStyle(), GroupingStyle.NONE);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_ASCII_DECIMAL_POINT_NO_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87654321.12345678");
    }

    public void test_ASCII_ASCII_DECIMAL_COMMA_GROUP3_DOT() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_ASCII_DECIMAL_COMMA_GROUP3_DOT_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87.654.321,123.456.78");
    }

    public void test_ASCII_DECIMAL_COMMA_GROUP3_SPACE() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) ' ');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_ASCII_DECIMAL_COMMA_GROUP3_SPACE_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87 654 321,123 456 78");
    }

    public void test_ASCII_DECIMAL_COMMA_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.NONE);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_ASCII_DECIMAL_COMMA_NO_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87654321,12345678");
    }

    public void test_LOCALIZED_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(style.getZeroCharacter(), null);
        assertEquals(style.getPositiveSignCharacter(), null);
        assertEquals(style.getNegativeSignCharacter(), null);
        assertEquals(style.getDecimalPointCharacter(), null);
        assertEquals(style.getGroupingCharacter(), null);
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), null);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_LOCALIZED_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87,654,321.123,456,78");
    }

    public void test_LOCALIZED_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
        assertEquals(style.getZeroCharacter(), null);
        assertEquals(style.getPositiveSignCharacter(), null);
        assertEquals(style.getNegativeSignCharacter(), null);
        assertEquals(style.getDecimalPointCharacter(), null);
        assertEquals(style.getGroupingCharacter(), null);
        assertEquals(style.getGroupingStyle(), GroupingStyle.NONE);
        assertEquals(style.getGroupingSize(), null);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_LOCALIZED_NO_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87654321.12345678");
    }

    public void test_print_groupBeforeDecimal() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals(test.print(MONEY), "87,654,321.12345678");
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    public void test_of_Locale_GB() {
        MoneyAmountStyle style = MoneyAmountStyle.of(TEST_GB_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) '.');
        assertEquals(style.getGroupingCharacter(), (Character) ',');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_of_Locale_DE() {
        MoneyAmountStyle style = MoneyAmountStyle.of(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    //-----------------------------------------------------------------------
    // localize(Locale)
    //-----------------------------------------------------------------------
    public void test_localize_GB() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_GB_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) '.');
        assertEquals(style.getGroupingCharacter(), (Character) ',');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_localize_DE() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_localize_DE_fixedZero() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '_');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_localize_DE_fixedPositive() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '_');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_localize_DE_fixedNegative() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '_');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_localize_DE_fixedDecimal() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) '_');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_localize_DE_fixedGrouping() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '0');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) ',');
        assertEquals(style.getGroupingCharacter(), (Character) '_');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    public void test_localize_DE_fixedZeroAndDecimal() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_').withDecimalPointCharacter('-');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals(style.getZeroCharacter(), (Character) '_');
        assertEquals(style.getPositiveSignCharacter(), (Character) '+');
        assertEquals(style.getNegativeSignCharacter(), (Character) '-');
        assertEquals(style.getDecimalPointCharacter(), (Character) '-');
        assertEquals(style.getGroupingCharacter(), (Character) '.');
        assertEquals(style.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(style.getGroupingSize(), (Integer) 3);
        assertEquals(style.isForcedDecimalPoint(), false);
    }

    //-----------------------------------------------------------------------
    // With
    //-----------------------------------------------------------------------
    public void test_withZeroCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getZeroCharacter(), null);
        MoneyAmountStyle test = base.withZeroCharacter('_');
        assertEquals(base.getZeroCharacter(), null);
        assertEquals(test.getZeroCharacter(), (Character) '_');
    }

    public void test_withZeroCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals(base.getZeroCharacter(), (Character) '0');
        MoneyAmountStyle test = base.withZeroCharacter('0');
        assertSame(test, base);
    }

    public void test_withZeroCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getZeroCharacter(), null);
        MoneyAmountStyle test = base.withZeroCharacter(null);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withPositiveSignCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getPositiveSignCharacter(), null);
        MoneyAmountStyle test = base.withPositiveSignCharacter('_');
        assertEquals(base.getPositiveSignCharacter(), null);
        assertEquals(test.getPositiveSignCharacter(), (Character) '_');
    }

    public void test_withPositiveSignCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals(base.getPositiveSignCharacter(), (Character) '+');
        MoneyAmountStyle test = base.withPositiveSignCharacter('+');
        assertSame(test, base);
    }

    public void test_withPositiveSignCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getPositiveSignCharacter(), null);
        MoneyAmountStyle test = base.withPositiveSignCharacter(null);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withNegativeSignCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getNegativeSignCharacter(), null);
        MoneyAmountStyle test = base.withNegativeSignCharacter('_');
        assertEquals(base.getNegativeSignCharacter(), null);
        assertEquals(test.getNegativeSignCharacter(), (Character) '_');
    }

    public void test_withNegativeSignCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals(base.getNegativeSignCharacter(), (Character) '-');
        MoneyAmountStyle test = base.withNegativeSignCharacter('-');
        assertSame(test, base);
    }

    public void test_withNegativeSignCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getNegativeSignCharacter(), null);
        MoneyAmountStyle test = base.withNegativeSignCharacter(null);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withDecimalPointCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getDecimalPointCharacter(), null);
        MoneyAmountStyle test = base.withDecimalPointCharacter('_');
        assertEquals(base.getDecimalPointCharacter(), null);
        assertEquals(test.getDecimalPointCharacter(), (Character) '_');
    }

    public void test_withDecimalPointCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals(base.getDecimalPointCharacter(), (Character) '.');
        MoneyAmountStyle test = base.withDecimalPointCharacter('.');
        assertSame(test, base);
    }

    public void test_withDecimalPointCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getDecimalPointCharacter(), null);
        MoneyAmountStyle test = base.withDecimalPointCharacter(null);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withGroupingCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getGroupingCharacter(), null);
        MoneyAmountStyle test = base.withGroupingCharacter('_');
        assertEquals(base.getGroupingCharacter(), null);
        assertEquals(test.getGroupingCharacter(), (Character) '_');
    }

    public void test_withGroupingCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals(base.getGroupingCharacter(), (Character) ',');
        MoneyAmountStyle test = base.withGroupingCharacter(',');
        assertSame(test, base);
    }

    public void test_withGroupingCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getGroupingCharacter(), null);
        MoneyAmountStyle test = base.withGroupingCharacter(null);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withGroupingStyle() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getGroupingStyle(), GroupingStyle.FULL);
        MoneyAmountStyle test = base.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertEquals(base.getGroupingStyle(), GroupingStyle.FULL);
        assertEquals(test.getGroupingStyle(), GroupingStyle.BEFORE_DECIMAL_POINT);
    }

    public void test_withGroupingStyle_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getGroupingStyle(), GroupingStyle.FULL);
        MoneyAmountStyle test = base.withGroupingStyle(GroupingStyle.FULL);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    public void test_withGroupingSize() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getGroupingSize(), null);
        MoneyAmountStyle test = base.withGroupingSize(6);
        assertEquals(base.getGroupingSize(), null);
        assertEquals(test.getGroupingSize(), (Integer) 6);
    }

    public void test_withGroupingSize_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals(base.getGroupingSize(), (Integer) 3);
        MoneyAmountStyle test = base.withGroupingSize(3);
        assertSame(test, base);
    }

    public void test_withGroupingSize_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.getGroupingSize(), null);
        MoneyAmountStyle test = base.withGroupingSize(null);
        assertSame(test, base);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withGroupingSize_negative() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        base.withGroupingSize(-1);
    }

    //-----------------------------------------------------------------------
    public void test_withForcedDecimalPoint() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.isForcedDecimalPoint(), false);
        MoneyAmountStyle test = base.withForcedDecimalPoint(true);
        assertEquals(base.isForcedDecimalPoint(), false);
        assertEquals(test.isForcedDecimalPoint(), true);
    }

    public void test_withForcedDecimalPoint_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(base.isForcedDecimalPoint(), false);
        MoneyAmountStyle test = base.withForcedDecimalPoint(false);
        assertSame(test, base);
    }

    //-----------------------------------------------------------------------
    // equals
    //-----------------------------------------------------------------------
    public void test_equals_same() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(a.equals(a), true);
    }

    public void test_equals_otherType() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(a.equals(""), false);
    }

    public void test_equals_null() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(a.equals(null), false);
    }

    public void test_equals_equal_zeroChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_zeroChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_equal_positiveChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_positiveChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_equal_negativeChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_negativeChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_equal_decimalPointChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_decimalPointChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_equal_groupingChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_groupingChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_equal_groupingStyle() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_groupingStyle() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.NONE);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_equal_groupingSize() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_groupingSize() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    public void test_equals_equal_forcedDecimalPoint_false() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_equal_forcedDecimalPoint_true() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertEquals(a.equals(b), true);
        assertEquals(b.equals(a), true);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_equals_notEqual_forcedDecimalPoint() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertEquals(a.equals(b), false);
        assertEquals(b.equals(a), false);
    }

    //-----------------------------------------------------------------------
    public void test_toString() {
        MoneyAmountStyle test = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertTrue(test.toString().startsWith("MoneyAmountStyle"));
    }

}
