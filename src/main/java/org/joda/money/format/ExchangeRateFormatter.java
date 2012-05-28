package org.joda.money.format;

import java.io.IOException;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.Locale;

import org.joda.money.ExchangeRate;
import org.joda.money.format.PrintParseUtils.ComponentsMissingException;
import org.joda.money.format.PrintParseUtils.ParseException;

/**
 * A formatter object that knows how to print and parse {@link ExchangeRate} objects.
 * <p>
 * Use {@link ExchangeRateFormatterBuilder} objects to create new instances of this class.
 * </p>
 * 
 * @author Tom Pasierb
 */
public final class ExchangeRateFormatter extends BaseFormatter<ExchangeRatePrinter, ExchangeRateParser> implements
		Serializable {

	private static final long serialVersionUID = 294648124294L;

	/**
	 * Scale to be used when printing the given exchange rate.
	 */
	private int scale;

	/**
	 * Rounding mode to be used when printing the given exchange rate.
	 */
	private RoundingMode roundingMode;

	/**
	 * The exponent of 10 that will be used when calculating a number of target units and modifying the exchange rate's
	 * rate accordingly. Used only when printing.
	 */
	private int targetUnitsExponent = 0;

	/**
	 * Creates an instance of the object capable of printing and parsing exchange rates.
	 * 
	 * @param locale
	 * @param printers
	 * @param parsers
	 * @param scale
	 * @param roundingMode
	 * @param targetUnitsExponent
	 */
	ExchangeRateFormatter(Locale locale, ExchangeRatePrinter[] printers, ExchangeRateParser[] parsers, int scale,
			RoundingMode roundingMode, int targetUnitsExponent) {
		super(locale, printers, parsers);
		assert scale >= -1;
		assert roundingMode != null;
		assert targetUnitsExponent >= 0;
		this.scale = scale;
		this.roundingMode = roundingMode;
		this.targetUnitsExponent = targetUnitsExponent;
	}

	/**
	 * Returns a copy of this instance with the specified locale.
	 * <p>
	 * Changing the locale may change the style of output depending on how the formatter has been configured.
	 * 
	 * @param locale
	 *            the locale, not null
	 * @return the new instance, never null
	 */
	public ExchangeRateFormatter withLocale(Locale locale) {
		checkNotNull(locale, "Locale must not be null");
		return new ExchangeRateFormatter(locale, printers, parsers, scale, roundingMode, targetUnitsExponent);
	}

	public ExchangeRate parse(CharSequence input) {
		checkNotNull(input, "Text must not be null");
		ExchangeRateParseContext result = parse(input, 0);
		try {
			PrintParseUtils.verifyParseContext(result);
		} catch (ComponentsMissingException e) {
			throw new ExchangeRateFormatException(
					"Parsing did not find all required components for creating an exchange rate, any of source currency, target currency or rate are missing: "
							+ e.getMessage(), e.getCause());
		} catch (ParseException e) {
			throw new ExchangeRateFormatException(e.getMessage(), e.getCause());
		}
		return result.toExchangeRate();
	}

	private ExchangeRateParseContext parse(CharSequence input, int startIndex) {
		checkNotNull(input, "Text must not be null");
		if (startIndex < 0 || startIndex > input.length()) {
			throw new StringIndexOutOfBoundsException("Invalid start index: " + startIndex);
		}
		if (isParser() == false) {
			throw new UnsupportedOperationException("MoneyFomatter has not been configured to be able to parse");
		}
		ExchangeRateParseContext context = new ExchangeRateParseContext(locale, input, startIndex);
		for (ExchangeRateParser parser : parsers) {
			parser.parse(context);
			if (context.isError()) {
				break;
			}
		}
		return context;
	}

	public String print(ExchangeRate exchangeRate) {
		StringBuilder buf = new StringBuilder();
		print(buf, exchangeRate);
		return buf.toString();
	}

	public void print(Appendable appendable, ExchangeRate exchangeRate) throws ExchangeRateFormatException {
		try {
			printIO(appendable, exchangeRate);
		} catch (IOException e) {
			throw new ExchangeRateFormatException(e.getMessage(), e);
		}
	}

	public void printIO(Appendable appendable, ExchangeRate exchangeRate) throws IOException {
		checkNotNull(exchangeRate, "exchangeRate must not be null");
		if (isPrinter() == false) {
			throw new UnsupportedOperationException("ExchangeRateFormatter has not been configured to be able to print");
		}

		ExchangeRate val = exchangeRate;
		if (this.targetUnitsExponent > 0) {
			val = val.withRate(val.getRate().movePointRight(targetUnitsExponent));
		}

		if (this.scale >= 0) {
			val = val.withRate(val.getRate().setScale(scale, roundingMode));
		}

		ExchangeRatePrintContext context = new ExchangeRatePrintContext(locale, targetUnitsExponent);
		for (ExchangeRatePrinter printer : printers) {
			printer.print(context, appendable, val);
		}
	}

}
