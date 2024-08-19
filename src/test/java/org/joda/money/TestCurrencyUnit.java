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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Test CurrencyUnit.
 */
class TestCurrencyUnit {

    private static final Currency JDK_GBP = Currency.getInstance("GBP");

    //-----------------------------------------------------------------------
    // registeredCurrencies()
    //-----------------------------------------------------------------------
    @Test
    void test_registeredCurrencies() {
        var curList = CurrencyUnit.registeredCurrencies();
        var found = false;
        for (CurrencyUnit currencyUnit : curList) {
            if (currencyUnit.getCode().equals("GBP")) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    void test_registeredCurrencies_sorted() {
        var curList1 = CurrencyUnit.registeredCurrencies();
        var curList2 = CurrencyUnit.registeredCurrencies();
        Collections.sort(curList2);
        assertThat(curList1).isEqualTo(curList2);
        Collections.shuffle(curList2);
        Collections.sort(curList2);
        assertThat(curList1).isEqualTo(curList2);
    }

    @Test
    void test_registeredCurrency_nullCode() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency(null, 991, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidStringCode_empty() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("", 991, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidStringCode_1letter() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("A", 991, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidStringCode_2letters() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("AB", 991, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidStringCode_4letters() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("ABCD", 991, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidStringCode_lowerCase() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("xxA", 991, 2, Arrays.asList("xx")));
    }

    @Test
    void test_registeredCurrency_invalidStringCode_number() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("123", 991, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidStringCode_dash() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("A-", 991, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidNumericCode_small() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("TST", -2, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidNumericCode_big() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("TST", 1000, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidDP_small() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("TST", 991, -2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_invalidDP_big() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("TST", 991, 31, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_validDP_big() {
        CurrencyUnit.registerCurrency("XLG", -1, 30, new ArrayList<>());

        var currency = CurrencyUnit.of("XLG");
        assertThat(currency.getDecimalPlaces()).isEqualTo(30);
    }

    @Test
    void test_registeredCurrency_nullCountry() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("TST", 991, 2, Arrays.asList((String) null)));
    }

    @Test
    void test_registeredCurrency_alreadyRegisteredCode() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("GBP", 991, 2, Arrays.asList("GB")));
    }

    @Test
    void test_registeredCurrency_alreadyRegisteredNumericCode() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("TST", 826, 2, Arrays.asList("TS")));
    }

    @Test
    void test_registeredCurrency_alreadyRegisteredCountry() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> CurrencyUnit.registerCurrency("GBX", 991, 2, Arrays.asList("GB")));
    }

    @Test
    void test_registeredCurrencies_crossCheck() {
        var curList = CurrencyUnit.registeredCurrencies();
        for (CurrencyUnit currencyUnit : curList) {
            try {
                var curr = Currency.getInstance(currencyUnit.getCode());
                var dp = curr.getDefaultFractionDigits() < 0 ? 0 : curr.getDefaultFractionDigits();
                assertThat(currencyUnit.getDecimalPlaces()).as(curr.getCurrencyCode()).isEqualTo(dp);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    //-----------------------------------------------------------------------
    // registeredCountries()
    //-----------------------------------------------------------------------
    @Test
    void test_registeredCountries() {
        var countryList = CurrencyUnit.registeredCountries();
        assertThat(countryList).contains("GB");
        assertThat(countryList).contains("EU");
        assertThat(countryList).contains("US");
    }

    @Test
    void test_registeredCountries_sorted() {
        var curList1 = CurrencyUnit.registeredCountries();
        var curList2 = CurrencyUnit.registeredCountries();
        Collections.sort(curList2);
        assertThat(curList1).isEqualTo(curList2);
        Collections.shuffle(curList2);
        Collections.sort(curList2);
        assertThat(curList1).isEqualTo(curList2);
    }

    //-----------------------------------------------------------------------
    // constants
    //-----------------------------------------------------------------------
    @Test
    void test_constants() {
        assertThat(CurrencyUnit.of("USD")).isEqualTo(CurrencyUnit.USD);
        assertThat(CurrencyUnit.of("EUR")).isEqualTo(CurrencyUnit.EUR);
        assertThat(CurrencyUnit.of("JPY")).isEqualTo(CurrencyUnit.JPY);
        assertThat(CurrencyUnit.of("GBP")).isEqualTo(CurrencyUnit.GBP);
        assertThat(CurrencyUnit.of("CHF")).isEqualTo(CurrencyUnit.CHF);
        assertThat(CurrencyUnit.of("AUD")).isEqualTo(CurrencyUnit.AUD);
        assertThat(CurrencyUnit.of("CAD")).isEqualTo(CurrencyUnit.CAD);
    }

    //-----------------------------------------------------------------------
    // constructor assert
    //-----------------------------------------------------------------------
    @Test
    void test_constructor_nullCode() {
        assertThatExceptionOfType(AssertionError.class)
            .isThrownBy(() -> new CurrencyUnit(null, (short) 1, (short) 2));
    }

    //-----------------------------------------------------------------------
    // of(Currency)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_Currency() {
        var test = CurrencyUnit.of(JDK_GBP);
        assertThat(test.getCode()).isEqualTo("GBP");
    }

    @Test
    void test_factory_of_Currency_nullCurrency() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.of((Currency) null));
    }

    //-----------------------------------------------------------------------
    // of(String)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_String() {
        var test = CurrencyUnit.of("GBP");
        assertThat(test.getCode()).isEqualTo("GBP");
    }

    @Test
    void test_factory_of_String_nullString() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.of((String) null));
    }

    @Test
    void test_factory_of_String_unknownCurrency() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.of("ABC"))
            .withMessage("Unknown currency 'ABC'");
    }

    @Test
    void test_factory_of_String_empty() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.of(""));
    }

    @Test
    void test_factory_of_String_tooShort_unknown() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.of("AB"));
    }

    @Test
    void test_factory_of_String_tooLong_unknown() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.of("ABCD"));
    }

    //-----------------------------------------------------------------------
    // ofNumericCode(String)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofNumericCode_String() {
        var test = CurrencyUnit.ofNumericCode("826");
        assertThat(test.getCode()).isEqualTo("GBP");
    }

    @Test
    void test_factory_ofNumericCode_String_2char() {
        var test = CurrencyUnit.ofNumericCode("051");
        assertThat(test.getCode()).isEqualTo("AMD");
    }

    @Test
    void test_factory_ofNumericCode_String_2charNoPad() {
        var test = CurrencyUnit.ofNumericCode("51");
        assertThat(test.getCode()).isEqualTo("AMD");
    }

    @Test
    void test_factory_ofNumericCode_String_1char() {
        var test = CurrencyUnit.ofNumericCode("008");
        assertThat(test.getCode()).isEqualTo("ALL");
    }

    @Test
    void test_factory_ofNumericCode_String_1charNoPad() {
        var test = CurrencyUnit.ofNumericCode("8");
        assertThat(test.getCode()).isEqualTo("ALL");
    }

    @Test
    void test_factory_ofNumericCode_String_nullString() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode((String) null));
    }

    @Test
    void test_factory_ofNumericCode_String_unknownCurrency() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode("111"))
            .withMessage("Unknown currency '111'");
    }

    @Test
    void test_factory_ofNumericCode_String_negative() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode("-1"));
    }

    @Test
    void test_factory_ofNumericCode_String_empty() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode(""))
            .withMessage("Unknown currency ''");
    }

    @Test
    void test_factory_ofNumericCode_String_tooLong() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode("1234"))
            .withMessage("Unknown currency '1234'");
    }

    //-----------------------------------------------------------------------
    // ofNumericCode(int)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofNumericCode_int() {
        var test = CurrencyUnit.ofNumericCode(826);
        assertThat(test.getCode()).isEqualTo("GBP");
    }

    @Test
    void test_factory_ofNumericCode_int_2char() {
        var test = CurrencyUnit.ofNumericCode(51);
        assertThat(test.getCode()).isEqualTo("AMD");
    }

    @Test
    void test_factory_ofNumericCode_int_1char() {
        var test = CurrencyUnit.ofNumericCode(8);
        assertThat(test.getCode()).isEqualTo("ALL");
    }

    @Test
    void test_factory_ofNumericCode_int_unknownCurrency() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode(111))
            .withMessage("Unknown currency '111'");
    }

    @Test
    void test_factory_ofNumericCode_int_negative() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode(-1))
            .withMessage("Unknown currency '-1'");
    }

    @Test
    void test_factory_ofNumericCode_int_tooLong() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofNumericCode(1234))
            .withMessage("Unknown currency '1234'");
    }

    //-----------------------------------------------------------------------
    // of(Locale)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_of_LocaleUK() {
        var test = CurrencyUnit.of(Locale.UK);
        assertThat(test.getCode()).isEqualTo("GBP");
    }

    @Test
    void test_factory_of_LocaleUS() {
        var test = CurrencyUnit.of(Locale.US);
        assertThat(test.getCode()).isEqualTo("USD");
    }

    @Test
    void test_factory_of_Locale_nullLocale() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.of((Locale) null));
    }

    @Test
    void test_factory_of_Locale_unknownCurrency() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.of(Locale.of("en", "XY")))
            .withMessage("No currency found for locale 'en_XY'");
    }

    //-----------------------------------------------------------------------
    // ofCountry(String)
    //-----------------------------------------------------------------------
    @Test
    void test_factory_ofCountry_String() {
        var test = CurrencyUnit.ofCountry("GB");
        assertThat(test.getCode()).isEqualTo("GBP");
    }

    @Test
    void test_factory_ofCountry_String_nullString() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.ofCountry((String) null));
    }

    @Test
    void test_factory_ofCountry_String_unknownCurrency() {
        assertThatExceptionOfType(IllegalCurrencyException.class)
            .isThrownBy(() -> CurrencyUnit.ofCountry("gb"))
            .withMessage("No currency found for country 'gb'");
    }

    //-----------------------------------------------------------------------
    // Serialisation
    //-----------------------------------------------------------------------
    @Test
    void test_serialization() throws Exception {
        var cu = CurrencyUnit.of("GBP");
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(cu);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            var input = (CurrencyUnit) ois.readObject();
            assertThat(input).isEqualTo(cu);
        }
    }

    @Test
    void test_serialization_invalidNumericCode() throws IOException {
        var cu = new CurrencyUnit("GBP", (short) 234, (short) 2);
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(cu);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(() -> ois.readObject())
                .withMessageContaining("numeric code")
                .withMessageContaining("currency GBP");
        }
    }

    @Test
    void test_serialization_invalidDecimalPlaces() throws IOException {
        var cu = new CurrencyUnit("GBP", (short) 826, (short) 1);
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(cu);
            oos.close();
            var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertThatExceptionOfType(InvalidObjectException.class)
                .isThrownBy(() -> ois.readObject())
                .withMessageContaining("decimal places")
                .withMessageContaining("currency GBP");
        }
    }

    //-----------------------------------------------------------------------
    // getNumeric3Code()
    //-----------------------------------------------------------------------
    @Test
    void test_getNumeric3Code_GBP() {
        var test = CurrencyUnit.of("GBP");
        assertThat(test.getNumeric3Code()).isEqualTo("826");
    }

    @Test
    void test_getNumeric3Code_ALL() {
        var test = CurrencyUnit.of("ALL");
        assertThat(test.getNumeric3Code()).isEqualTo("008");
    }

    @Test
    void test_getNumeric3Code_AMD() {
        var test = CurrencyUnit.of("AMD");
        assertThat(test.getNumeric3Code()).isEqualTo("051");
    }

    @Test
    void test_getNumeric3Code_XFU() {
        var test = CurrencyUnit.of("XFU");
        assertThat(test.getNumeric3Code()).isEmpty();
    }

    //-----------------------------------------------------------------------
    // getNumericCode()
    //-----------------------------------------------------------------------
    @Test
    void test_getNumericCode_GBP() {
        var test = CurrencyUnit.of("GBP");
        assertThat(test.getNumericCode()).isEqualTo(826);
    }

    //-----------------------------------------------------------------------
    // getCurrencyCodes()
    //-----------------------------------------------------------------------
    @Test
    void test_getCurrencyCodes_GBP() {
        var test = CurrencyUnit.of("GBP").getCountryCodes();
        assertThat(test).contains("GB");
        assertThat(test).contains("IM");
        assertThat(test).contains("JE");
        assertThat(test).contains("GG");
    }

    //-----------------------------------------------------------------------
    // getDecimalPlaces()
    //-----------------------------------------------------------------------
    @Test
    void test_getDecimalPlaces_GBP() {
        var test = CurrencyUnit.of("GBP");
        assertThat(test.getDecimalPlaces()).isEqualTo(2);
    }

    @Test
    void test_getDecimalPlaces_JPY() {
        var test = CurrencyUnit.of("JPY");
        assertThat(test.getDecimalPlaces()).isEqualTo(0);
    }

    @Test
    void test_getDecimalPlaces_XXX() {
        var test = CurrencyUnit.of("XXX");
        assertThat(test.getDecimalPlaces()).isEqualTo(0);
    }

    //-----------------------------------------------------------------------
    // isPseudoCurrency()
    //-----------------------------------------------------------------------
    @Test
    void test_isPseudoCurrency_GBP() {
        var test = CurrencyUnit.of("GBP");
        assertThat(test.isPseudoCurrency()).isFalse();
    }

    @Test
    void test_isPseudoCurrency_JPY() {
        var test = CurrencyUnit.of("JPY");
        assertThat(test.isPseudoCurrency()).isFalse();
    }

    @Test
    void test_isPseudoCurrency_XXX() {
        var test = CurrencyUnit.of("XXX");
        assertThat(test.isPseudoCurrency()).isTrue();
    }

    //-----------------------------------------------------------------------
    // getSymbol()
    //-----------------------------------------------------------------------
    @Test
    void test_getSymbol_GBP() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("GBP");
            assertThat(test.getSymbol()).isEqualTo("\u00A3");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_JPY() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("JPY");
            assertThat(test.getSymbol()).contains("JP");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_TMT() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("TMT");
            assertThat(test.getSymbol()).isEqualTo("TMT");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_XXX() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("XXX");
            assertThat(test.getSymbol()).isEqualTo("XXX");
        } finally {
            Locale.setDefault(loc);
        }
    }

    //-----------------------------------------------------------------------
    // getSymbol()
    //-----------------------------------------------------------------------
    @Test
    void test_getSymbol_Locale_GBP_UK() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("GBP");
            assertThat(test.getSymbol(Locale.UK)).isEqualTo("\u00A3");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_GBP_France() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("GBP");
            assertThat(test.getSymbol(Locale.FRANCE)).contains("GB");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_USD_UK() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("USD");
            assertThat(test.getSymbol(Locale.US)).isEqualTo("$");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_USD_France() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("USD");
            assertThat(test.getSymbol(Locale.FRANCE)).contains("US");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_JPY_Japan() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("JPY");
            assertThat(test.getSymbol(Locale.JAPAN)).isEqualTo("\uFFE5");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_TMT_UK() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("TMT");
            assertThat(test.getSymbol(Locale.UK)).isEqualTo("TMT");
        } finally {
            Locale.setDefault(loc);
        }
    }

    @Test
    void test_getSymbol_Locale_XXX() {
        var loc = Locale.getDefault();
        try {
            Locale.setDefault(Locale.UK);
            var test = CurrencyUnit.of("XXX");
            assertThat(test.getSymbol(Locale.FRANCE)).isEqualTo("XXX");
            test = CurrencyUnit.of("XXX");
            assertThat(test.getSymbol(Locale.US)).isEqualTo("XXX");
        } finally {
            Locale.setDefault(loc);
        }
    }

    //-----------------------------------------------------------------------
    // toCurrency()
    //-----------------------------------------------------------------------
    @Test
    void test_toCurrency() {
        var test = CurrencyUnit.of("GBP");
        assertThat(test.toCurrency()).isEqualTo(JDK_GBP);
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    @Test
    void test_compareTo() {
        var a = CurrencyUnit.of("EUR");
        var b = CurrencyUnit.of("GBP");
        var c = CurrencyUnit.of("JPY");
        assertThat(a.compareTo(a)).isEqualTo(0);
        assertThat(b.compareTo(b)).isEqualTo(0);
        assertThat(c.compareTo(c)).isEqualTo(0);

        assertThat(a.compareTo(b) < 0).isTrue();
        assertThat(b.compareTo(a) > 0).isTrue();

        assertThat(a.compareTo(c) < 0).isTrue();
        assertThat(c.compareTo(a) > 0).isTrue();

        assertThat(b.compareTo(c) < 0).isTrue();
        assertThat(c.compareTo(b) > 0).isTrue();
    }

    @Test
    void test_compareTo_null() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> CurrencyUnit.of("EUR").compareTo(null));
    }

    //-----------------------------------------------------------------------
    // equals() hashCode()
    //-----------------------------------------------------------------------
    @Test
    void test_equals_hashCode() {
        var a = CurrencyUnit.of("GBP");
        var b = CurrencyUnit.of("GBP");
        var c = CurrencyUnit.of("EUR");
        assertThat(a).isEqualTo(a);
        assertThat(b).isEqualTo(b);
        assertThat(c).isEqualTo(c);

        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(b.hashCode()).isEqualTo(a.hashCode());

        assertThat(c).isNotEqualTo(a);
        assertThat(c).isNotEqualTo(b);
    }

    @Test
    void test_equals_false() {
        var a = CurrencyUnit.of("GBP");
        assertThat(a).isNotEqualTo(null);
        Object obj = "String";  // avoid warning in Eclipse
        assertThat(obj).isNotEqualTo(a);
        assertThat(new Object()).isNotEqualTo(a);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @Test
    void test_toString() {
        var test = CurrencyUnit.of("GBP");
        assertThat(test).hasToString("GBP");
    }

}
