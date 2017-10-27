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

import java.util.List;

import org.junit.Test;

/**
 * Test CurrencyUnit.
 */
public class TestCurrencyUnitExtension {

    @Test
    public void test_CurrencyFromMoneyData() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("GBP")) {
                found = true;
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void test_CurrencyFromMoneyDataExtension() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("BTC")) {
                found = true;
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void test_LargerDecimalPrecisionCurrencyFromMoneyDataExtension() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("ETH")) {
                found = true;
                assertEquals("ETH 1.234567890000000000000000000000", Money.of(currencyUnit, 1.23456789d).toString());
                break;
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void test_InvalidLargerDecimalPrecisionCurrencyFromMoneyDataExtension() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("XXL")) {
                found = true;
                break;
            }
        }
        assertEquals(false, found);
    }

    @Test
    public void test_CurrencyMissing() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("NMC")) {
                found = true;
                break;
            }
        }
        assertEquals(false, found);
    }

    @Test
    public void test_CurrencyEURChanged() {
        CurrencyUnit currency = CurrencyUnit.ofCountry("HU");
        assertEquals(CurrencyUnit.EUR, currency);
        assertEquals(true, CurrencyUnit.EUR.getCountryCodes().contains("HU"));
        assertEquals(true, CurrencyUnit.of("HUF").getCountryCodes().isEmpty());
    }

}
