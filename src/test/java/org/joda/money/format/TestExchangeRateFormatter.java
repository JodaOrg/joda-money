package org.joda.money.format;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.ExchangeRate;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class TestExchangeRateFormatter {

	private ExchangeRateFormatter formatter;
	private ExchangeRateFormatter cannotPrintFormatter;
	private ExchangeRateFormatter formatterWithTargetUnitsExponent;
	private ExchangeRateFormatter formatterWithScale;
	private ExchangeRateFormatter formatterWithScaleNoRounding;
	private ExchangeRateFormatter formatterAllComponents;
	
	private Locale locale = new Locale("pl");
	
	@BeforeTest
	public void setup() {
		formatter = new ExchangeRateFormatterBuilder()
			.appendTargetCurrency().appendLiteral("/")
			.appendSourceCurrency().appendLiteral(" ")
			.appendRate()
			.toFormatter(locale);
		
		formatterWithTargetUnitsExponent = new ExchangeRateFormatterBuilder()
			.setTargetUnitsExponent(2)
			.appendTargetUnitCount().appendLiteral(":")
			.appendRate().toFormatter(locale);
		
		formatterWithScale = new ExchangeRateFormatterBuilder()
			.appendRate().setScale(3, RoundingMode.HALF_UP)
			.toFormatter(locale);
		
		formatterWithScaleNoRounding = new ExchangeRateFormatterBuilder()
			.appendRate().setScale(3, RoundingMode.UNNECESSARY)
			.toFormatter(locale);
		
		formatterAllComponents = new ExchangeRateFormatterBuilder()
			.appendTargetUnitCount().appendLiteral(" ")
			.appendTargetCurrency().appendLiteral("/")
			.appendSourceCurrency().appendLiteral(" ")
			.appendRate()
			.toFormatter(locale);
		
		
		cannotPrintFormatter = new ExchangeRateFormatterBuilder().append(null, new ExchangeRateParser() {
			public void parse(ExchangeRateParseContext context) {
			}
		}).toFormatter(locale);
		
	}
	
	@Test
	public void test_print_exchangeRate() {
		ExchangeRate rate = ExchangeRate.of(new BigDecimal("4.1927"), CurrencyUnit.getInstance("PLN"), CurrencyUnit.EUR);
		assertEquals(formatter.print(rate), "EUR/PLN 4,1927");
	}
	
	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void test_print_exchangeRate_cannotPrint() {
		ExchangeRate rate = ExchangeRate.of(new BigDecimal("4.1927"), CurrencyUnit.getInstance("PLN"), CurrencyUnit.EUR);
		cannotPrintFormatter.print(rate);
	}
	
	@Test(expectedExceptions = NullPointerException.class)
	public void test_print_exchangeRate_nullInput() {
		cannotPrintFormatter.print(null);
	}
	
	@Test
	public void test_print_exchangeRate_scaleLowerThanGivenRate() {
		ExchangeRate rate = ExchangeRate.of(new BigDecimal("4.1927"), CurrencyUnit.getInstance("PLN"), CurrencyUnit.EUR);
		assertEquals(formatterWithScale.print(rate), "4,193");
	}
	
	@Test(expectedExceptions = ArithmeticException.class)
	public void test_print_exchangeRate_scaleLowerThanGivenRateCannotRound() {
		ExchangeRate rate = ExchangeRate.of(new BigDecimal("4.1927"), CurrencyUnit.getInstance("PLN"), CurrencyUnit.EUR);
		formatterWithScaleNoRounding.print(rate);
	}
	
	@Test
	public void test_print_exchangeRate_targetUnitsDifferentThanOne() {
		ExchangeRate rate = ExchangeRate.of(new BigDecimal("0.041455"), CurrencyUnit.getInstance("PLN"), CurrencyUnit.JPY);
		assertEquals(formatterWithTargetUnitsExponent.print(rate), "100:4,1455");
	}
	
	@Test
	public void test_parseExchangeRateWithCurrenciesAndLiterals() {
		StringBuilder input = new StringBuilder("USD/EUR 0,8154");
		ExchangeRate parsed = formatter.parse(input);
		ExchangeRate expected = ExchangeRate.of(new BigDecimal("0.8154"), CurrencyUnit.EUR, CurrencyUnit.USD);
		assertEquals(parsed, expected);
	}
	
	@Test
	public void test_parse_includingTargetUnitCount() {
		StringBuilder input = new StringBuilder("100 JPY/PLN 4,1455");
		ExchangeRate parsed = formatterAllComponents.parse(input);
		ExchangeRate expected = ExchangeRate.of(new BigDecimal("0.041455"), CurrencyUnit.getInstance("PLN"), CurrencyUnit.JPY);
		assertEquals(parsed, expected);
	}
	
	@Test(expectedExceptions = ExchangeRateFormatException.class)
	public void test_parse_requiredComponentsMissing() {
		StringBuilder input = new StringBuilder("0,8154");
		formatterWithScale.parse(input);
	}
	
	@Test
	public void test_toString() {
		ExchangeRateFormatter formatter = new ExchangeRateFormatterBuilder()
		.appendTargetUnitCount().appendLiteral(":")
		.appendTargetCurrency().appendLiteral("/")
		.appendSourceCurrency().appendLiteral(" ")
		.appendRate()
		.toFormatter();
		assertEquals(formatter.toString(), "${targetUnitCount}':'${target}'/'${source}' '${rate}");
	}
}
