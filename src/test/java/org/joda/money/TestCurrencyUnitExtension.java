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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test CurrencyUnit.
 */
class TestCurrencyUnitExtension {

    @Test
    void test_CurrencyFromMoneyData() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("GBP")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void test_CurrencyFromMoneyDataExtension() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("BTC")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void test_LargerDecimalPrecisionCurrencyFromMoneyDataExtension() {
        List<CurrencyUnit> curList = CurrencyUnit.registeredCurrencies();
        boolean found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("ETH")) {
                found = true;
                assertEquals("ETH 1.234567890000000000000000000000", Money.of(currencyUnit, 1.23456789d).toString());
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void test_InvalidLargerDecimalPrecisionCurrencyFromMoneyDataExtension() {
        for (CurrencyUnit currencyUnit : CurrencyUnit.registeredCurrencies()) {
            if (currencyUnit.getCode().equals("XXL")) {
                fail("Currency XXL should not exist");
            }
        }
    }

    @Test
    void test_CurrencyMissing() {
        for (CurrencyUnit currencyUnit : CurrencyUnit.registeredCurrencies()) {
            if (currencyUnit.getCode().equals("NMC")) {
                fail("Currency NMC should not exist");
            }
        }
    }

    @Test
    void test_CurrencyEURChanged() {
        CurrencyUnit currency = CurrencyUnit.ofCountry("HU");
        assertEquals(CurrencyUnit.EUR, currency);
        assertTrue(CurrencyUnit.EUR.getCountryCodes().contains("HU"));
        assertTrue(CurrencyUnit.of("HUF").getCountryCodes().isEmpty());
    }

}
