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

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.testng.annotations.Test;

/**
 * Test MoneyParseContext.
 */
@Test
public class TestMoneyParseContext {

    public void test_initialState() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(test.getAmount(), null);
        assertEquals(test.getCurrency(), null);
        assertEquals(test.getIndex(), 0);
        assertEquals(test.getErrorIndex(), -1);
        assertEquals(test.getText().toString(), "GBP 123");
        assertEquals(test.getTextLength(), 7);
        assertEquals(test.isError(), false);
        assertEquals(test.isFullyParsed(), false);
        assertEquals(test.isComplete(), false);
        ParsePosition pp = new ParsePosition(0);
        pp.setErrorIndex(-1);
        assertEquals(test.toParsePosition(), pp);
    }

    public void test_setIndex() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(test.getIndex(), 0);
        test.setIndex(2);
        assertEquals(test.getIndex(), 2);
    }

    public void test_setErrorIndex() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(test.getErrorIndex(), -1);
        test.setErrorIndex(3);
        assertEquals(test.getErrorIndex(), 3);
    }

    public void test_setError() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(test.getIndex(), 0);
        assertEquals(test.getErrorIndex(), -1);
        test.setError();
        assertEquals(test.getIndex(), 0);
        assertEquals(test.getErrorIndex(), 0);
    }

    public void test_setError_withIndex() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(test.getIndex(), 0);
        assertEquals(test.getErrorIndex(), -1);
        test.setIndex(2);
        test.setError();
        assertEquals(test.getIndex(), 2);
        assertEquals(test.getErrorIndex(), 2);
    }

    //-----------------------------------------------------------------------
    public void test_isComplete_noCurrency() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setAmount(BigDecimal.TEN);
        assertEquals(test.isComplete(), false);
    }

    public void test_isComplete_noAmount() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setCurrency(CurrencyUnit.GBP);
        assertEquals(test.isComplete(), false);
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_toBigMoney_noCurrency() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setAmount(BigDecimal.TEN);
        test.toBigMoney();
    }

    @Test(expectedExceptions = MoneyFormatException.class)
    public void test_toBigMoney_noAmount() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setCurrency(CurrencyUnit.GBP);
        test.toBigMoney();
    }

    //-----------------------------------------------------------------------
    public void test_getTextSubstring_ok() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(test.getTextSubstring(0, 2), "GB");
        assertEquals(test.getTextSubstring(5, 7), "23");
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void test_getTextSubstring_beforeStart() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.getTextSubstring(-1, 2);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void test_getTextSubstring_afterEnd() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.getTextSubstring(0, 8);
    }

}
