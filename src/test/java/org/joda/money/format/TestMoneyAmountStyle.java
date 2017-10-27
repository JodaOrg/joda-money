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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test MoneyAmountStyle.
 */
public class TestMoneyAmountStyle {

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_DE_LOCALE = new Locale("de", "DE", "TEST");
    private static final BigMoney MONEY = BigMoney.of(CurrencyUnit.GBP, new BigDecimal("87654321.12345678"));

    @Before
    public void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
    }

    @After
    public void afterMethod() {
        Locale.setDefault(cCachedLocale);
    }

    //-----------------------------------------------------------------------
    // Constants
    //-----------------------------------------------------------------------
    @Test
    public void test_ASCII_DECIMAL_POINT_GROUP3_COMMA() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) '.', style.getDecimalPointCharacter());
        assertEquals((Character) ',', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_ASCII_DECIMAL_POINT_GROUP3_COMMA_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87,654,321.123,456,78", test.print(MONEY));
    }

    @Test
    public void test_ASCII_DECIMAL_POINT_GROUP3_SPACE() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) '.', style.getDecimalPointCharacter());
        assertEquals((Character) ' ', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_ASCII_ASCII_DECIMAL_POINT_GROUP3_SPACE_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87 654 321.123 456 78", test.print(MONEY));
    }

    @Test
    public void test_ASCII_DECIMAL_POINT_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) '.', style.getDecimalPointCharacter());
        assertEquals((Character) ',', style.getGroupingCharacter());
        assertEquals(GroupingStyle.NONE, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_ASCII_DECIMAL_POINT_NO_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87654321.12345678", test.print(MONEY));
    }

    @Test
    public void test_ASCII_ASCII_DECIMAL_COMMA_GROUP3_DOT() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_ASCII_DECIMAL_COMMA_GROUP3_DOT_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87.654.321,123.456.78", test.print(MONEY));
    }

    @Test
    public void test_ASCII_DECIMAL_COMMA_GROUP3_SPACE() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) ' ', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_ASCII_DECIMAL_COMMA_GROUP3_SPACE_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_SPACE;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87 654 321,123 456 78", test.print(MONEY));
    }

    @Test
    public void test_ASCII_DECIMAL_COMMA_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.NONE, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_ASCII_DECIMAL_COMMA_NO_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.ASCII_DECIMAL_COMMA_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87654321,12345678", test.print(MONEY));
    }

    @Test
    public void test_LOCALIZED_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, style.getZeroCharacter());
        assertEquals(null, style.getPositiveSignCharacter());
        assertEquals(null, style.getNegativeSignCharacter());
        assertEquals(null, style.getDecimalPointCharacter());
        assertEquals(null, style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals(null, style.getGroupingSize());
        assertEquals(null, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_LOCALIZED_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87,654,321.123,456,78", test.print(MONEY));
    }

    @Test
    public void test_LOCALIZED_NO_GROUPING() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
        assertEquals(null, style.getZeroCharacter());
        assertEquals(null, style.getPositiveSignCharacter());
        assertEquals(null, style.getNegativeSignCharacter());
        assertEquals(null, style.getDecimalPointCharacter());
        assertEquals(null, style.getGroupingCharacter());
        assertEquals(GroupingStyle.NONE, style.getGroupingStyle());
        assertEquals(null, style.getGroupingSize());
        assertEquals(null, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_LOCALIZED_NO_GROUPING_print() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87654321.12345678", test.print(MONEY));
    }

    @Test
    public void test_print_groupBeforeDecimal() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyFormatter test = new MoneyFormatterBuilder().appendAmount(style).toFormatter();
        assertEquals("87,654,321.12345678", test.print(MONEY));
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    @Test
    public void test_of_Locale_GB() {
        MoneyAmountStyle style = MoneyAmountStyle.of(TEST_GB_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) '.', style.getDecimalPointCharacter());
        assertEquals((Character) ',', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_of_Locale_DE() {
        MoneyAmountStyle style = MoneyAmountStyle.of(TEST_DE_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    //-----------------------------------------------------------------------
    // localize(Locale)
    //-----------------------------------------------------------------------
    @Test
    public void test_localize_GB() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_GB_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) '.', style.getDecimalPointCharacter());
        assertEquals((Character) ',', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_localize_DE() {
        MoneyAmountStyle style = MoneyAmountStyle.LOCALIZED_GROUPING.localize(TEST_DE_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_localize_DE_fixedZero() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals((Character) '_', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_localize_DE_fixedPositive() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '_', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_localize_DE_fixedNegative() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '_', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_localize_DE_fixedDecimal() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) '_', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_localize_DE_fixedGrouping() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals((Character) '0', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) ',', style.getDecimalPointCharacter());
        assertEquals((Character) '_', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    @Test
    public void test_localize_DE_fixedZeroAndDecimal() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_').withDecimalPointCharacter('-');
        MoneyAmountStyle style = base.localize(TEST_DE_LOCALE);
        assertEquals((Character) '_', style.getZeroCharacter());
        assertEquals((Character) '+', style.getPositiveSignCharacter());
        assertEquals((Character) '-', style.getNegativeSignCharacter());
        assertEquals((Character) '-', style.getDecimalPointCharacter());
        assertEquals((Character) '.', style.getGroupingCharacter());
        assertEquals(GroupingStyle.FULL, style.getGroupingStyle());
        assertEquals((Integer) 3, style.getGroupingSize());
        assertEquals((Integer) 0, style.getExtendedGroupingSize());
        assertEquals(false, style.isForcedDecimalPoint());
    }

    //-----------------------------------------------------------------------
    // With
    //-----------------------------------------------------------------------
    @Test
    public void test_withZeroCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getZeroCharacter());
        MoneyAmountStyle test = base.withZeroCharacter('_');
        assertEquals(null, base.getZeroCharacter());
        assertEquals((Character) '_', test.getZeroCharacter());
    }

    @Test
    public void test_withZeroCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals((Character) '0', base.getZeroCharacter());
        MoneyAmountStyle test = base.withZeroCharacter('0');
        assertSame(base, test);
    }

    @Test
    public void test_withZeroCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getZeroCharacter());
        MoneyAmountStyle test = base.withZeroCharacter(null);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withPositiveSignCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getPositiveSignCharacter());
        MoneyAmountStyle test = base.withPositiveSignCharacter('_');
        assertEquals(null, base.getPositiveSignCharacter());
        assertEquals((Character) '_', test.getPositiveSignCharacter());
    }

    @Test
    public void test_withPositiveSignCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals((Character) '+', base.getPositiveSignCharacter());
        MoneyAmountStyle test = base.withPositiveSignCharacter('+');
        assertSame(base, test);
    }

    @Test
    public void test_withPositiveSignCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getPositiveSignCharacter());
        MoneyAmountStyle test = base.withPositiveSignCharacter(null);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withNegativeSignCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getNegativeSignCharacter());
        MoneyAmountStyle test = base.withNegativeSignCharacter('_');
        assertEquals(null, base.getNegativeSignCharacter());
        assertEquals((Character) '_', test.getNegativeSignCharacter());
    }

    @Test
    public void test_withNegativeSignCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals((Character) '-', base.getNegativeSignCharacter());
        MoneyAmountStyle test = base.withNegativeSignCharacter('-');
        assertSame(base, test);
    }

    @Test
    public void test_withNegativeSignCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getNegativeSignCharacter());
        MoneyAmountStyle test = base.withNegativeSignCharacter(null);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withDecimalPointCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getDecimalPointCharacter());
        MoneyAmountStyle test = base.withDecimalPointCharacter('_');
        assertEquals(null, base.getDecimalPointCharacter());
        assertEquals((Character) '_', test.getDecimalPointCharacter());
    }

    @Test
    public void test_withDecimalPointCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals((Character) '.', base.getDecimalPointCharacter());
        MoneyAmountStyle test = base.withDecimalPointCharacter('.');
        assertSame(base, test);
    }

    @Test
    public void test_withDecimalPointCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getDecimalPointCharacter());
        MoneyAmountStyle test = base.withDecimalPointCharacter(null);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withGroupingCharacter() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getGroupingCharacter());
        MoneyAmountStyle test = base.withGroupingCharacter('_');
        assertEquals(null, base.getGroupingCharacter());
        assertEquals((Character) '_', test.getGroupingCharacter());
    }

    @Test
    public void test_withGroupingCharacter_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals((Character) ',', base.getGroupingCharacter());
        MoneyAmountStyle test = base.withGroupingCharacter(',');
        assertSame(base, test);
    }

    @Test
    public void test_withGroupingCharacter_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getGroupingCharacter());
        MoneyAmountStyle test = base.withGroupingCharacter(null);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withGroupingStyle() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(GroupingStyle.FULL, base.getGroupingStyle());
        MoneyAmountStyle test = base.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertEquals(GroupingStyle.FULL, base.getGroupingStyle());
        assertEquals(GroupingStyle.BEFORE_DECIMAL_POINT, test.getGroupingStyle());
    }

    @Test
    public void test_withGroupingStyle_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(GroupingStyle.FULL, base.getGroupingStyle());
        MoneyAmountStyle test = base.withGroupingStyle(GroupingStyle.FULL);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withGroupingSize() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getGroupingSize());
        MoneyAmountStyle test = base.withGroupingSize(6);
        assertEquals(null, base.getGroupingSize());
        assertEquals((Integer) 6, test.getGroupingSize());
    }

    @Test
    public void test_withGroupingSize_same() {
        MoneyAmountStyle base = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        assertEquals((Integer) 3, base.getGroupingSize());
        MoneyAmountStyle test = base.withGroupingSize(3);
        assertSame(base, test);
    }

    @Test
    public void test_withGroupingSize_sameNull() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(null, base.getGroupingSize());
        MoneyAmountStyle test = base.withGroupingSize(null);
        assertSame(base, test);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_withGroupingSize_negative() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        base.withGroupingSize(-1);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withForcedDecimalPoint() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(false, base.isForcedDecimalPoint());
        MoneyAmountStyle test = base.withForcedDecimalPoint(true);
        assertEquals(false, base.isForcedDecimalPoint());
        assertEquals(true, test.isForcedDecimalPoint());
    }

    @Test
    public void test_withForcedDecimalPoint_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(false, base.isForcedDecimalPoint());
        MoneyAmountStyle test = base.withForcedDecimalPoint(false);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_withAbsValue() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(false, base.isAbsValue());
        MoneyAmountStyle test = base.withAbsValue(true);
        assertEquals(false, base.isAbsValue());
        assertEquals(true, test.isAbsValue());
    }

    @Test
    public void test_withAbsValue_same() {
        MoneyAmountStyle base = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(false, base.isAbsValue());
        MoneyAmountStyle test = base.withAbsValue(false);
        assertSame(base, test);
    }

    //-----------------------------------------------------------------------
    // equals
    //-----------------------------------------------------------------------
    @Test
    public void test_equals_same() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(true, a.equals(a));
    }

    @Test
    public void test_equals_otherType() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(false, a.equals(""));
    }

    @Test
    public void test_equals_null() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertEquals(false, a.equals(null));
    }

    @Test
    public void test_equals_equal_zeroChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_zeroChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withZeroCharacter('_');
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_positiveChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_positiveChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withPositiveSignCharacter('_');
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_negativeChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_negativeChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withNegativeSignCharacter('_');
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_decimalPointChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_decimalPointChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withDecimalPointCharacter('_');
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_groupingChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_groupingChar() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingCharacter('_');
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_groupingStyle() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_groupingStyle() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingStyle(GroupingStyle.NONE);
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_groupingSize() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_groupingSize() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withGroupingSize(4);
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_forcedDecimalPoint_false() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true).withForcedDecimalPoint(false);
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_equal_forcedDecimalPoint_true() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_forcedDecimalPoint() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withForcedDecimalPoint(true);
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    @Test
    public void test_equals_equal_absValue_false() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true).withAbsValue(false);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true).withAbsValue(false);
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_equal_absValue_true() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        assertEquals(true, a.equals(b));
        assertEquals(true, b.equals(a));
        assertEquals(b.hashCode(), a.hashCode());
    }

    @Test
    public void test_equals_notEqual_absValue() {
        MoneyAmountStyle a = MoneyAmountStyle.LOCALIZED_GROUPING;
        MoneyAmountStyle b = MoneyAmountStyle.LOCALIZED_GROUPING.withAbsValue(true);
        assertEquals(false, a.equals(b));
        assertEquals(false, b.equals(a));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_toString() {
        MoneyAmountStyle test = MoneyAmountStyle.LOCALIZED_GROUPING;
        assertTrue(test.toString().startsWith("MoneyAmountStyle"));
    }

}
