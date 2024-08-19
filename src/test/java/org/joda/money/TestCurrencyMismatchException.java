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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test CurrencyMismatchException.
 */
class TestCurrencyMismatchException {

    private static final CurrencyUnit GBP = CurrencyUnit.of("GBP");
    private static final CurrencyUnit EUR = CurrencyUnit.of("EUR");

    //-----------------------------------------------------------------------
    // new (CurrencyUnit,CurrencyUnit)
    //-----------------------------------------------------------------------
    @Test
    void test_new_GBPEUR() {
        var test = new CurrencyMismatchException(GBP, EUR);
        assertThat(test.getMessage()).isEqualTo("Currencies differ: GBP/EUR");
        assertThat(test.getCause()).isNull();
        assertThat(test.getFirstCurrency()).isEqualTo(GBP);
        assertThat(test.getSecondCurrency()).isEqualTo(EUR);
    }

    @Test
    void test_new_nullEUR() {
        var test = new CurrencyMismatchException(null, EUR);
        assertThat(test.getMessage()).isEqualTo("Currencies differ: null/EUR");
        assertThat(test.getCause()).isNull();
        assertThat(test.getFirstCurrency()).isNull();
        assertThat(test.getSecondCurrency()).isEqualTo(EUR);
    }

    @Test
    void test_new_GBPnull() {
        var test = new CurrencyMismatchException(GBP, null);
        assertThat(test.getMessage()).isEqualTo("Currencies differ: GBP/null");
        assertThat(test.getCause()).isNull();
        assertThat(test.getFirstCurrency()).isEqualTo(GBP);
        assertThat(test.getSecondCurrency()).isNull();
    }

    @Test
    void test_new_nullnull() {
        var test = new CurrencyMismatchException(null, null);
        assertThat(test.getMessage()).isEqualTo("Currencies differ: null/null");
        assertThat(test.getCause()).isNull();
        assertThat(test.getFirstCurrency()).isNull();
        assertThat(test.getSecondCurrency()).isNull();
    }

}
