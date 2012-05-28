package org.joda.money;

import static org.testng.Assert.fail;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.money.CurrencyUnit;
import org.joda.money.ExchangeRate;
import org.joda.money.Money;
import org.joda.money.NoCommonCurrencyException;
import org.joda.money.NotExchangeableException;
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
		ExchangeRate.of(new BigDecimal("1.2"), EUR, CurrencyUnit.USD, -1, RoundingMode.HALF_UP);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void test_factory_of_null_rounding_mode() {
		ExchangeRate.of(new BigDecimal("1.2"), EUR, CurrencyUnit.USD, 2, null);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void test_factory_of_same_currencies_rate_not_one() {
		ExchangeRate.of(new BigDecimal("1.2"), EUR, EUR);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void test_exchange_nullOtherExchangeRate() {
		Money toExchange = null;
		USD_RATE.exchange(toExchange);
	}

	@Test(expectedExceptions = NotExchangeableException.class)
	public void test_money_not_exchangeable_by_exchangeRate() {
		Money amountInUSD = Money.of(CurrencyUnit.USD, new BigDecimal("23.56"));
		PLN_TO_EUR_RATE.exchange(amountInUSD);
	}

	@Test
	public void testPlnToUsd() {
		Money a1 = Money.of(PLN, new BigDecimal("24.555"), RoundingMode.HALF_UP);

		Money result = USD_RATE.exchange(a1);

		assertEquals(USD_RATE.getTarget(), result.getCurrencyUnit());
		assertEquals(new BigDecimal("10.48"), result.getAmount());
	}

	@Test
	public void testUsdToPln() {

		Money a1 = Money.of(CurrencyUnit.USD, new BigDecimal("24.56"));

		Money result = USD_RATE.exchange(a1);

		assertEquals(USD_RATE.getSource(), result.getCurrencyUnit());
		assertEquals(new BigDecimal("57.54"), result.getAmount());
	}

	@Test
	public void testPlnToJpy() {

		Money a1 = Money.of(PLN, new BigDecimal("49.89"));

		Money result = JPY_RATE.exchange(a1);

		assertEquals(0, result.getAmount().scale());
		assertEquals(JPY_RATE.getTarget(), result.getCurrencyUnit());
		assertEquals(new BigDecimal("2234"), result.getAmount());
	}

	@Test
	public void testJpyToPln() {

		Money a1 = Money.of(CurrencyUnit.JPY, new BigDecimal("9811"));

		Money result = JPY_RATE.exchange(a1);

		assertEquals(2, result.getAmount().scale());
		assertEquals(JPY_RATE.getSource(), result.getCurrencyUnit());
		assertEquals(new BigDecimal("219.14"), result.getAmount());
	}

	@Test
	public void test_exchange_using_identity_rate() {
		ExchangeRate identityRate = ExchangeRate.of(new BigDecimal("1"), PLN, PLN);
		Money a = Money.of(PLN, new BigDecimal("23.11"));
		Money exchanged = identityRate.exchange(a);
		assertTrue(exchanged.getAmount().compareTo(a.getAmount()) == 0);
	}

	@Test
	public void testPrecisionSmallerThanGivenRate() {
		ExchangeRate rate = ExchangeRate.of(new BigDecimal("3.3741"), PLN, CurrencyUnit.USD, 2, RoundingMode.HALF_UP);
		assertTrue(new BigDecimal("0.3").compareTo(rate.invert().getRate()) == 0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testSameCurrencyRateDifferentThanOne() {
		ExchangeRate.of(new BigDecimal("3"), PLN, PLN);
		fail("construction of an exchange rate between the same source and target currencies and rate different than one should not be possible!");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void combineNull() {
		ExchangeRate reversed = USD_RATE.invert();
		reversed.combine(null);
	}

	@Test(expectedExceptions = NoCommonCurrencyException.class)
	public void combineInvalid() {
		ExchangeRate reversed = USD_RATE.invert();
		ExchangeRate other = ExchangeRate.of(new BigDecimal(2.11), CurrencyUnit.JPY, CurrencyUnit.getInstance("NOK"));

		reversed.combine(other);
	}

	@Test
	public void combineValid() {
		ExchangeRate combined = PLN_TO_EUR_RATE.combine(GBP_RATE);
		assertEquals(CurrencyUnit.EUR, combined.getTarget());
		assertEquals(CurrencyUnit.GBP, combined.getSource());
		BigDecimal expectedRate = PLN_TO_EUR_RATE.getRate().divide(GBP_RATE.getRate(), ExchangeRate.DEFAULT_SCALE,
				ExchangeRate.DEFAULT_ROUNDING_MODE);
		assertTrue(expectedRate.compareTo(combined.getRate()) == 0);

		Money m = Money.of(CurrencyUnit.EUR, new BigDecimal("30"));
		Money o = combined.exchange(m);

		assertEquals(new BigDecimal("24.12"), o.getAmount());
		assertEquals(CurrencyUnit.GBP, o.getCurrencyUnit());
	}

	@Test
	public void combineCommentExample() {
		ExchangeRate USD = ExchangeRate.of(new BigDecimal("3.50"), PLN, CurrencyUnit.USD);
		ExchangeRate EUR = ExchangeRate.of(new BigDecimal("4.00"), PLN, CurrencyUnit.EUR);
		ExchangeRate combined = USD.combine(EUR);

		assertNotNull(combined);
		assertTrue(new BigDecimal("0.875").compareTo(combined.getRate()) == 0);
		assertEquals(CurrencyUnit.EUR, combined.getSource());
		assertEquals(CurrencyUnit.USD, combined.getTarget());
	}

	@Test
	public void combineExchangeRatesOnlyRateDifferent() {
		ExchangeRate EUR1 = ExchangeRate.of(new BigDecimal("3.22"), PLN, CurrencyUnit.EUR);
		ExchangeRate EUR2 = ExchangeRate.of(new BigDecimal("3.19"), PLN, CurrencyUnit.EUR);
		ExchangeRate combined = EUR1.combine(EUR2);

		// EUR1's target should become the combined rate's target currency
		assertNotNull(combined);
		assertTrue(new BigDecimal("1").compareTo(combined.getRate()) == 0);
		assertEquals(CurrencyUnit.EUR, combined.getSource());
		assertEquals(CurrencyUnit.EUR, combined.getTarget());

		EUR1 = ExchangeRate.of(new BigDecimal("3.22"), PLN, CurrencyUnit.EUR);
		EUR2 = ExchangeRate.of(new BigDecimal("0.3135"), CurrencyUnit.EUR, PLN);
		combined = EUR1.combine(EUR2);

		// EUR1's target should become the combined rate's target currency ... no matter the other's target and source
		// positions
		assertNotNull(combined);
		assertTrue(new BigDecimal("1").compareTo(combined.getRate()) == 0);
		assertEquals(CurrencyUnit.EUR, combined.getSource());
		assertEquals(CurrencyUnit.EUR, combined.getTarget());
	}

}
