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

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.junit.Test;

/**
 * Test MoneyParseContext.
 */
public class TestMoneyParseContext {

    @Test
    public void test_initialState() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(null, test.getAmount());
        assertEquals(null, test.getCurrency());
        assertEquals(0, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        assertEquals("GBP 123", test.getText().toString());
        assertEquals(7, test.getTextLength());
        assertEquals(false, test.isError());
        assertEquals(false, test.isFullyParsed());
        assertEquals(false, test.isComplete());
        ParsePosition pp = new ParsePosition(0);
        pp.setErrorIndex(-1);
        assertEquals(pp, test.toParsePosition());
    }

    @Test
    public void test_setIndex() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(0, test.getIndex());
        test.setIndex(2);
        assertEquals(2, test.getIndex());
    }

    @Test
    public void test_setErrorIndex() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(-1, test.getErrorIndex());
        test.setErrorIndex(3);
        assertEquals(3, test.getErrorIndex());
    }

    @Test
    public void test_setError() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(0, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        test.setError();
        assertEquals(0, test.getIndex());
        assertEquals(0, test.getErrorIndex());
    }

    @Test
    public void test_setError_withIndex() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals(0, test.getIndex());
        assertEquals(-1, test.getErrorIndex());
        test.setIndex(2);
        test.setError();
        assertEquals(2, test.getIndex());
        assertEquals(2, test.getErrorIndex());
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_isComplete_noCurrency() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setAmount(BigDecimal.TEN);
        assertEquals(false, test.isComplete());
    }

    @Test
    public void test_isComplete_noAmount() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setCurrency(CurrencyUnit.GBP);
        assertEquals(false, test.isComplete());
    }

    @Test(expected = MoneyFormatException.class)
    public void test_toBigMoney_noCurrency() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setAmount(BigDecimal.TEN);
        test.toBigMoney();
    }

    @Test(expected = MoneyFormatException.class)
    public void test_toBigMoney_noAmount() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.setCurrency(CurrencyUnit.GBP);
        test.toBigMoney();
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_getTextSubstring_ok() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        assertEquals("GB", test.getTextSubstring(0, 2));
        assertEquals("23", test.getTextSubstring(5, 7));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_getTextSubstring_beforeStart() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.getTextSubstring(-1, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void test_getTextSubstring_afterEnd() {
        MoneyParseContext test = new MoneyParseContext(Locale.FRANCE, "GBP 123", 0);
        test.getTextSubstring(0, 8);
    }

}
