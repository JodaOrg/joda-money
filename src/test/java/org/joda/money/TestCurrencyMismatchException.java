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
package org.joda.money;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test CurrencyMismatchException.
 */
public class TestCurrencyMismatchException {

    private static final CurrencyUnit GBP = CurrencyUnit.of("GBP");
    private static final CurrencyUnit EUR = CurrencyUnit.of("EUR");

    //-----------------------------------------------------------------------
    // new (CurrencyUnit,CurrencyUnit)
    //-----------------------------------------------------------------------
    @Test
    public void test_new_GBPEUR() {
        CurrencyMismatchException test = new CurrencyMismatchException(GBP, EUR);
        assertEquals("Currencies differ: GBP/EUR", test.getMessage());
        assertEquals(null, test.getCause());
        assertEquals(GBP, test.getFirstCurrency());
        assertEquals(EUR, test.getSecondCurrency());
    }

    @Test
    public void test_new_nullEUR() {
        CurrencyMismatchException test = new CurrencyMismatchException(null, EUR);
        assertEquals("Currencies differ: null/EUR", test.getMessage());
        assertEquals(null, test.getCause());
        assertEquals(null, test.getFirstCurrency());
        assertEquals(EUR, test.getSecondCurrency());
    }

    @Test
    public void test_new_GBPnull() {
        CurrencyMismatchException test = new CurrencyMismatchException(GBP, null);
        assertEquals("Currencies differ: GBP/null", test.getMessage());
        assertEquals(null, test.getCause());
        assertEquals(GBP, test.getFirstCurrency());
        assertEquals(null, test.getSecondCurrency());
    }

    @Test
    public void test_new_nullnull() {
        CurrencyMismatchException test = new CurrencyMismatchException(null, null);
        assertEquals("Currencies differ: null/null", test.getMessage());
        assertEquals(null, test.getCause());
        assertEquals(null, test.getFirstCurrency());
        assertEquals(null, test.getSecondCurrency());
    }

}
