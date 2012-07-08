/*
 *  Copyright 2009-2011 Stephen Colebourne
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.testng.annotations.Test;

@Test
public class TestExchangeRate {

    private static CurrencyUnit PLN = CurrencyUnit.of("PLN");
    private static CurrencyUnit EUR = CurrencyUnit.EUR;

    private ExchangeRate PLN_TO_EUR_RATE = ExchangeRate.of(new BigDecimal("3.3710"), PLN, CurrencyUnit.EUR);
    private ExchangeRate USD_RATE = ExchangeRate.of(new BigDecimal("2.3428"), PLN, CurrencyUnit.USD);
    private ExchangeRate JPY_RATE = ExchangeRate.of(new BigDecimal("0.022336"), PLN, CurrencyUnit.JPY);
    private ExchangeRate GBP_RATE = ExchangeRate.of(new BigDecimal("4.1921"), PLN, CurrencyUnit.GBP);

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_identity_nullCurrency() {
        ExchangeRate.identity(null);
    }

    @Test
    public void test_factory_of_identity_notNullCurrency() {
        ExchangeRate identity = ExchangeRate.identity(CurrencyUnit.EUR);
        assertTrue(BigDecimal.ONE.compareTo(identity.getRate()) == 0);
        assertEquals(identity.getSource(), CurrencyUnit.EUR);
        assertEquals(identity.getTarget(), CurrencyUnit.EUR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_null_rate() {
        ExchangeRate.of(null, EUR, EUR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_null_source() {
        ExchangeRate.of(new BigDecimal("1.2"), null, EUR);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_null_target() {
        ExchangeRate.of(new BigDecimal("1.2"), EUR, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_negative_scale() {
        ExchangeRate.of(new BigDecimal("1.2"), EUR, CurrencyUnit.USD).operations(-1, RoundingMode.HALF_UP);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_factory_of_null_rounding_mode() {
        ExchangeRate.of(new BigDecimal("1.2"), EUR, CurrencyUnit.USD).operations(2, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_same_currencies_rate_not_one() {
        ExchangeRate.of(new BigDecimal("1.2"), EUR, EUR);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_rate_zero() {
        ExchangeRate.of(new BigDecimal("0"), EUR, CurrencyUnit.USD);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_factory_of_rate_less_than_zero() {
        ExchangeRate.of(new BigDecimal("-0.01"), EUR, CurrencyUnit.USD);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_with_rate_zero() {
        USD_RATE.withRate(new BigDecimal("0"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_with_rate_less_than_zero() {
        USD_RATE.withRate(new BigDecimal("-1"));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_exchange_nullOtherExchangeRate() {
        Money toExchange = null;
        USD_RATE.operations().exchange(toExchange);
    }

    @Test(expectedExceptions = NotExchangeableException.class)
    public void test_money_not_exchangeable_by_exchangeRate() {
        Money amountInUSD = Money.of(CurrencyUnit.USD, new BigDecimal("23.56"));
        PLN_TO_EUR_RATE.operations().exchange(amountInUSD);
    }

    @Test
    public void testPlnToUsd() {
        Money a1 = Money.of(PLN, new BigDecimal("24.555"), RoundingMode.HALF_UP);

        Money result = USD_RATE.operations().exchange(a1);

        assertEquals(USD_RATE.getTarget(), result.getCurrencyUnit());
        assertEquals(new BigDecimal("10.48"), result.getAmount());
    }

    @Test
    public void testUsdToPln() {

        Money a1 = Money.of(CurrencyUnit.USD, new BigDecimal("24.56"));

        Money result = USD_RATE.operations().exchange(a1);

        assertEquals(USD_RATE.getSource(), result.getCurrencyUnit());
        assertEquals(new BigDecimal("57.54"), result.getAmount());
    }

    @Test
    public void testPlnToJpy() {

        Money a1 = Money.of(PLN, new BigDecimal("49.89"));

        Money result = JPY_RATE.operations().exchange(a1);

        assertEquals(0, result.getAmount().scale());
        assertEquals(JPY_RATE.getTarget(), result.getCurrencyUnit());
        assertEquals(new BigDecimal("2234"), result.getAmount());
    }

    @Test
    public void testJpyToPln() {

        Money a1 = Money.of(CurrencyUnit.JPY, new BigDecimal("9811"));

        Money result = JPY_RATE.operations().exchange(a1);

        assertEquals(2, result.getAmount().scale());
        assertEquals(JPY_RATE.getSource(), result.getCurrencyUnit());
        assertEquals(new BigDecimal("219.14"), result.getAmount());
    }

    @Test
    public void test_exchange_using_identity_rate() {
        ExchangeRate identityRate = ExchangeRate.of(new BigDecimal("1"), PLN, PLN);
        Money a = Money.of(PLN, new BigDecimal("23.11"));
        Money exchanged = identityRate.operations().exchange(a);
        assertTrue(exchanged.getAmount().compareTo(a.getAmount()) == 0);
    }

    @Test
    public void testPrecisionSmallerThanGivenRate() {
        ExchangeRate rate = ExchangeRate.of(new BigDecimal("3.3741"), PLN, CurrencyUnit.USD);
        assertTrue(new BigDecimal("0.3").compareTo(rate.operations(2, RoundingMode.HALF_EVEN).invert().getExchangeRate().getRate()) == 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSameCurrencyRateDifferentThanOne() {
        ExchangeRate.of(new BigDecimal("3"), PLN, PLN);
        fail("construction of an exchange rate between the same source and target currencies and rate different than one should not be possible!");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void combineNull() {
        ExchangeRate reversed = USD_RATE.operations().invert().getExchangeRate();
        reversed.operations().combine(null);
    }

    @Test(expectedExceptions = NoCommonCurrencyException.class)
    public void combineInvalid() {
        ExchangeRate reversed = USD_RATE.operations().invert().getExchangeRate();
        ExchangeRate other = ExchangeRate.of(new BigDecimal(2.11), CurrencyUnit.JPY, CurrencyUnit.getInstance("NOK"));

        reversed.operations().combine(other);
    }

    @Test
    public void combineValid() {
        ExchangeRate combined = PLN_TO_EUR_RATE.operations().combine(GBP_RATE).getExchangeRate();
        assertEquals(CurrencyUnit.EUR, combined.getTarget());
        assertEquals(CurrencyUnit.GBP, combined.getSource());
        BigDecimal expectedRate = PLN_TO_EUR_RATE.getRate().divide(GBP_RATE.getRate(), 16, RoundingMode.HALF_EVEN);
        assertTrue(expectedRate.compareTo(combined.getRate()) == 0);

        Money m = Money.of(CurrencyUnit.EUR, new BigDecimal("30"));
        Money o = combined.operations().exchange(m);

        assertEquals(new BigDecimal("24.12"), o.getAmount());
        assertEquals(CurrencyUnit.GBP, o.getCurrencyUnit());
    }

    @Test
    public void combineCommentExample() {
        ExchangeRate USD = ExchangeRate.of(new BigDecimal("3.50"), PLN, CurrencyUnit.USD);
        ExchangeRate EUR = ExchangeRate.of(new BigDecimal("4.00"), PLN, CurrencyUnit.EUR);
        ExchangeRate combined = USD.operations().combine(EUR).getExchangeRate();

        assertNotNull(combined);
        assertTrue(new BigDecimal("0.875").compareTo(combined.getRate()) == 0);
        assertEquals(CurrencyUnit.EUR, combined.getSource());
        assertEquals(CurrencyUnit.USD, combined.getTarget());
    }

    @Test
    public void combineExchangeRatesOnlyRateDifferent() {
        ExchangeRate EUR1 = ExchangeRate.of(new BigDecimal("3.22"), PLN, CurrencyUnit.EUR);
        ExchangeRate EUR2 = ExchangeRate.of(new BigDecimal("3.19"), PLN, CurrencyUnit.EUR);
        ExchangeRate combined = EUR1.operations().combine(EUR2).getExchangeRate();

        // EUR1's target should become the combined rate's target currency
        assertNotNull(combined);
        assertTrue(new BigDecimal("1").compareTo(combined.getRate()) == 0);
        assertEquals(CurrencyUnit.EUR, combined.getSource());
        assertEquals(CurrencyUnit.EUR, combined.getTarget());

        EUR1 = ExchangeRate.of(new BigDecimal("3.22"), PLN, CurrencyUnit.EUR);
        EUR2 = ExchangeRate.of(new BigDecimal("0.3135"), CurrencyUnit.EUR, PLN);
        combined = EUR1.operations().combine(EUR2).getExchangeRate();

        // EUR1's target should become the combined rate's target currency ... no matter the other's target and source
        // positions
        assertNotNull(combined);
        assertTrue(new BigDecimal("1").compareTo(combined.getRate()) == 0);
        assertEquals(CurrencyUnit.EUR, combined.getSource());
        assertEquals(CurrencyUnit.EUR, combined.getTarget());
    }

    @Test
    public void test_serialization() throws IOException, ClassNotFoundException {
        ExchangeRate rateOut = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rateOut);

        byte[] arr = baos.toByteArray();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(arr));
        ExchangeRate rateIn = (ExchangeRate) ois.readObject();

        assertNotNull(rateIn);
        assertEquals(rateOut, rateIn);
    }

    @Test(expectedExceptions = InvalidObjectException.class)
    public void test_deserialize_malicious_stream() throws IOException, ClassNotFoundException {
        // serialized form of ExchangeRate.of(new BigDecimal("-0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);
        byte[] in = new byte[] { (byte) 0xac, (byte) 0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x12, 0x6f, 0x72, 0x67, 0x2e, 0x6a, 0x6f, 0x64,
                0x61, 0x2e, 0x6d, 0x6f, 0x6e, 0x65, 0x79, 0x2e, 0x53, 0x65, 0x72, 0x73, (byte) 0x8e, 0x13, (byte) 0xd0, (byte) 0xe9, 0x6b,
                (byte) 0xa0, (byte) 0xe2, 0x0c, 0x00, 0x00, 0x78, 0x70, 0x77, 0x1c, 0x45, 0x00, 0x03, 0x55, 0x53, 0x44, 0x03, 0x48, 0x00,
                0x02, 0x00, 0x03, 0x45, 0x55, 0x52, 0x03, (byte) 0xd2, 0x00, 0x02, 0x00, 0x00, 0x00, 0x01, (byte) 0xa5, 0x00, 0x00, 0x00,
                0x02, 0x78 };
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(in));
        ois.readObject();
    }

    @Test
    public void test_hashcode_should_be_identical_for_equal_instances() {
        ExchangeRate one = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);
        ExchangeRate two = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);

        assertEquals(one, two);
        assertTrue(one.hashCode() == two.hashCode());
    }

    @Test
    public void test_same_instances_should_be_equal() {
        ExchangeRate one = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);
        assertEquals(one, one);
    }

    @Test
    public void test_equals_object_of_other_type() {
        ExchangeRate one = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);
        assertFalse(one.equals(new BigDecimal("0")));
    }

    @Test
    public void test_equals_when_rate_differs() {
        ExchangeRate one = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);
        ExchangeRate two = ExchangeRate.of(new BigDecimal("0.92"), CurrencyUnit.USD, CurrencyUnit.EUR);
        assertFalse(one.equals(two));
    }

    @Test
    public void test_equals_when_source_currency_differs() {
        ExchangeRate one = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);
        ExchangeRate two = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.CHF, CurrencyUnit.EUR);
        assertFalse(one.equals(two));
    }

    @Test
    public void test_equals_when_target_currency_differs() {
        ExchangeRate one = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.EUR);
        ExchangeRate two = ExchangeRate.of(new BigDecimal("0.91"), CurrencyUnit.USD, CurrencyUnit.CHF);
        assertFalse(one.equals(two));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_parse_null_input() {
        ExchangeRate.parse(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_parse_illegal_input() {
        ExchangeRate.parse("ILLEGAL_INPUT");
    }

    @Test
    public void test_parse_legal_input_needs_trimming() {
        ExchangeRate rate = ExchangeRate.parse("\t1 EUR = 3.24 PLN  \t  ");
        assertEquals(rate.getTarget(), CurrencyUnit.EUR);
        assertEquals(rate.getSource(), PLN);
        assertEquals(rate.getRate(), new BigDecimal("3.24"));
    }

    @Test
    public void test_parse_legal_input_regular_case() {
        ExchangeRate rate = ExchangeRate.parse("1 GBP = 1.5485 USD");
        assertEquals(rate.getTarget(), CurrencyUnit.GBP);
        assertEquals(rate.getSource(), CurrencyUnit.USD);
        assertEquals(rate.getRate(), new BigDecimal("1.5485"));
    }

    @Test
    public void test_parse_legal_input_no_fractional_part() {
        ExchangeRate rate = ExchangeRate.parse("1 USD = 6 DKK");
        assertEquals(rate.getTarget(), CurrencyUnit.USD);
        assertEquals(rate.getSource(), CurrencyUnit.getInstance("DKK"));
        assertEquals(rate.getRate(), new BigDecimal("6"));
    }
}
