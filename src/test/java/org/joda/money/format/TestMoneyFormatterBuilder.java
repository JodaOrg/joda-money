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

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test MoneyFormatterBuilder.
 */
@Test
public class TestMoneyFormatterBuilder {

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
    private static final Money JPY_2345 = Money.parse("JPY 2345");

    private static final Locale cCachedLocale = Locale.getDefault();
    private static final Locale TEST_GB_LOCALE = new Locale("en", "GB", "TEST");
    private static final Locale TEST_FR_LOCALE = new Locale("fr", "FR", "TEST");
    private static final DecimalFormatSymbols FR_SYMBOLS = new DecimalFormatSymbols(Locale.FRANCE);
    private static final char FR_DECIMAL = FR_SYMBOLS.getMonetaryDecimalSeparator();
    private static final char FR_GROUP = FR_SYMBOLS.getGroupingSeparator();

    private MoneyFormatterBuilder iBuilder;

    @BeforeMethod
    public void beforeMethod() {
        Locale.setDefault(TEST_GB_LOCALE);
        iBuilder = new MoneyFormatterBuilder();
    }

    @AfterMethod
    public void afterMethod() {
        Locale.setDefault(cCachedLocale);
        iBuilder = null;
    }

    //-----------------------------------------------------------------------
    public void test_empty() {
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "");
        assertEquals(test.toString(), "");
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencyCode_print() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "GBP");
        assertEquals(test.toString(), "${code}");
    }

    public void test_appendCurrencyCode_parse_ok() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("GBP", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), CurrencyUnit.GBP);
    }

    public void test_appendCurrencyCode_parse_tooShort() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("GB", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyCode_parse_empty() {
        iBuilder.appendCurrencyCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencyNumeric3Code_print() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "826");
        assertEquals(test.toString(), "${numeric3Code}");
    }

    public void test_appendCurrencyNumeric3Code_parse_ok() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("826A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), CurrencyUnit.GBP);
    }

    public void test_appendCurrencyNumeric3Code_parse_tooShort() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("82", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumeric3Code_parse_badCurrency() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("991A", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumeric3Code_parse_empty() {
        iBuilder.appendCurrencyNumeric3Code();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencyNumericCode_print() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "826");
        assertEquals(test.toString(), "${numericCode}");
    }

    public void test_appendCurrencyNumericCode_parse_ok() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("826A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), CurrencyUnit.GBP);
    }

    public void test_appendCurrencyNumericCode_parse_ok_padded() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("008A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 3);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency().getCode(), "ALL");
    }

    public void test_appendCurrencyNumericCode_parse_ok_notPadded1() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("8A", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 1);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency().getCode(), "ALL");
    }

    public void test_appendCurrencyNumericCode_parse_ok_notPadded2() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("51 ", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 2);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency().getCode(), "AMD");
    }

    public void test_appendCurrencyNumericCode_parse_tooShort() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumericCode_parse_badCurrency() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("991A", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendCurrencyNumericCode_parse_empty() {
        iBuilder.appendCurrencyNumericCode();
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendCurrencySymbolLocalized_print() {
        iBuilder.appendCurrencySymbolLocalized();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "\u00a3");
        assertEquals(test.toString(), "${symbolLocalized}");
    }

    public void test_appendCurrencySymbolLocalized_parse() {
        iBuilder.appendCurrencySymbolLocalized();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isParser(), false);
    }

    //-----------------------------------------------------------------------
    public void test_appendLiteral_print() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "Hello");
        assertEquals(test.toString(), "'Hello'");
    }

    public void test_appendLiteral_print_empty() {
        iBuilder.appendLiteral("");
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "");
        assertEquals(test.toString(), "");
    }

    public void test_appendLiteral_print_null() {
        iBuilder.appendLiteral((CharSequence) null);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "");
        assertEquals(test.toString(), "");
    }

    public void test_appendLiteral_parse_ok() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("HelloWorld", 0);
        assertEquals(parsed.isError(), false);
        assertEquals(parsed.getIndex(), 5);
        assertEquals(parsed.getErrorIndex(), -1);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendLiteral_parse_tooShort() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("Hell", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    public void test_appendLiteral_parse_noMatch() {
        iBuilder.appendLiteral("Hello");
        MoneyFormatter test = iBuilder.toFormatter();
        MoneyParseContext  parsed = test.parse("Helol", 0);
        assertEquals(parsed.isError(), true);
        assertEquals(parsed.getIndex(), 0);
        assertEquals(parsed.getErrorIndex(), 0);
        assertEquals(parsed.getAmount(), null);
        assertEquals(parsed.getCurrency(), null);
    }

    //-----------------------------------------------------------------------
    public void test_appendAmount_GBP_2_34() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2_34), "2.34");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_23_45() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_23_45), "23.45");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_234_56() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_234_56), "234.56");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_2345_67() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_2345_67), "2,345.67");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_1234567_89() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_1234567_89), "1,234,567.89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_1234_56789() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(GBP_1234_56789), "1,234.567,89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_GBP_1234_56789_France() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_1234_56789), "1,234.567,89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_JPY_2345() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.print(JPY_2345), "2,345");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmount_3dp_BHD() {
        iBuilder.appendAmount();
        MoneyFormatter test = iBuilder.toFormatter();
        Money money = Money.of(CurrencyUnit.getInstance("BHD"), 6345345.735d);
        assertEquals(test.print(money), "6,345,345.735");
    }

    //-----------------------------------------------------------------------
    public void test_appendAmountLocalized_GBP_2_34() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_2_34), "2" + FR_DECIMAL + "34");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_23_45() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_23_45), "23" + FR_DECIMAL + "45");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_234_56() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_234_56), "234" + FR_DECIMAL + "56");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_MINUS_234_56() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_MINUS_234_56), "-234" + FR_DECIMAL + "56");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_2345_67() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_2345_67), "2" + FR_GROUP + "345" + FR_DECIMAL + "67");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_1234567_89() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_1234567_89), "1" + FR_GROUP + "234" + FR_GROUP + "567" + FR_DECIMAL + "89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_1234_56789() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(GBP_1234_56789), "1" + FR_GROUP + "234" + FR_DECIMAL + "567" + FR_GROUP + "89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_GBP_1234_56789_US() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.US);
        assertEquals(test.print(GBP_1234_56789), "1,234.567,89");
        assertEquals(test.toString(), "${amount}");
    }

    public void test_appendAmountLocalized_JPY_2345() {
        iBuilder.appendAmountLocalized();
        MoneyFormatter test = iBuilder.toFormatter(Locale.FRANCE);
        assertEquals(test.print(JPY_2345), "2" + FR_GROUP + "345");
        assertEquals(test.toString(), "${amount}");
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions = NullPointerException.class)
    public void test_appendAmount_MoneyAmountStyle_null() {
        iBuilder.appendAmount((MoneyAmountStyle) null);
    }

    @DataProvider(name = "appendAmount_MoneyAmountStyle")
    Object[][] data_appendAmount_MoneyAmountStyle() {
        MoneyAmountStyle noGrouping = MoneyAmountStyle.ASCII_DECIMAL_POINT_NO_GROUPING;
        MoneyAmountStyle group3Comma = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA;
        MoneyAmountStyle group3Space = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_SPACE;
        MoneyAmountStyle group3BeforeDp = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withGroupingStyle(GroupingStyle.BEFORE_DECIMAL_POINT);
        MoneyAmountStyle group3CommaForceDp = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withForcedDecimalPoint(true);
        MoneyAmountStyle group2Dash = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withGroupingSize(2).withGroupingCharacter('-');
        MoneyAmountStyle group4CommaAt = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA
                        .withGroupingSize(4).withDecimalPointCharacter('@').withForcedDecimalPoint(true);
        MoneyAmountStyle letters = MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA.withZeroCharacter('A');
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

    @Test(dataProvider = "appendAmount_MoneyAmountStyle")
    public void test_appendAmount_MoneyAmountStyle_GBP(
            MoneyAmountStyle style, String amount, String expected) {
        iBuilder.appendAmount(style);
        MoneyFormatter test = iBuilder.toFormatter();
        BigMoney money = BigMoney.of(GBP, new BigDecimal(amount));
        assertEquals(test.print(money), expected);
        assertEquals(test.parse(expected, 0).getAmount(), money.getAmount());
    }

    @Test(dataProvider = "appendAmount_MoneyAmountStyle")
    public void test_appendAmount_MoneyAmountStyle_JPY(
            MoneyAmountStyle style, String amount, String expected) {
        iBuilder.appendAmount(style);
        MoneyFormatter test = iBuilder.toFormatter();
        BigMoney money = BigMoney.of(JPY, new BigDecimal(amount));
        assertEquals(test.print(money), expected);
        assertEquals(test.parse(expected, 0).getAmount(), money.getAmount());
    }

    @Test(dataProvider = "appendAmount_MoneyAmountStyle")
    public void test_appendAmount_MoneyAmountStyle_BHD(
            MoneyAmountStyle style, String amount, String expected) {
        iBuilder.appendAmount(style);
        MoneyFormatter test = iBuilder.toFormatter();
        BigMoney money = BigMoney.of(BHD, new BigDecimal(amount));
        assertEquals(test.print(money), expected);
        assertEquals(test.parse(expected, 0).getAmount(), money.getAmount());
    }

    //-----------------------------------------------------------------------
    public void test_append_MoneyPrinterMoneyParser_printer() {
        MoneyPrinter printer = new MoneyPrinter() {
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append("HELLO");
            }
        };
        iBuilder.append(printer, null);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isPrinter(), true);
        assertEquals(test.isParser(), false);
        assertEquals(test.print(JPY_2345), "HELLO");
        assertEquals(test.toString().startsWith("org.joda.money.format.TestMoneyFormatterBuilder$"), true);
    }

    public void test_append_MoneyPrinterMoneyParser_parser() {
        MoneyParser parser = new MoneyParser() {
            public void parse(MoneyParseContext context) {
                context.setAmount(JPY_2345.getAmount());
                context.setCurrency(JPY_2345.getCurrencyUnit());
            }
        };
        iBuilder.append(null, parser);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isPrinter(), false);
        assertEquals(test.isParser(), true);
        assertEquals(test.parseMoney(""), JPY_2345);
        assertEquals(test.toString().startsWith("org.joda.money.format.TestMoneyFormatterBuilder$"), true);
    }

    public void test_append_MoneyPrinter_nullMoneyPrinter_nullMoneyParser() {
        iBuilder.append((MoneyPrinter) null, (MoneyParser) null);
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.isPrinter(), false);
        assertEquals(test.isParser(), false);
        assertEquals(test.toString(), "");
    }

    //-----------------------------------------------------------------------
    public void test_append_MoneyFormatter() {
        MoneyFormatter f1 = new MoneyFormatterBuilder().appendAmount().toFormatter();
        MoneyFormatter f2 = new MoneyFormatterBuilder().appendCurrencyCode().appendLiteral(" ").append(f1).toFormatter();
        assertEquals(f2.print(GBP_2345_67), "GBP 2,345.67");
    }

    //-----------------------------------------------------------------------
    public void test_toFormatter_defaultLocale() {
        MoneyFormatter test = iBuilder.toFormatter();
        assertEquals(test.getLocale(), TEST_GB_LOCALE);
    }

    public void test_toFormatter_Locale() {
        MoneyFormatter test = iBuilder.toFormatter(TEST_FR_LOCALE);
        assertEquals(test.getLocale(), TEST_FR_LOCALE);
    }

}
