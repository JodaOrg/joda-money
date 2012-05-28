package org.joda.money.format;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.ExchangeRate;
import org.joda.money.IllegalCurrencyException;
import org.joda.money.Utils;
import org.joda.money.format.PrintParseUtils.CurrencyUnitFormat;
import org.joda.money.format.PrintParseUtils.ParseCallback;

public final class ExchangeRateFormatterBuilder {

	private List<ExchangeRatePrinter> printers = new ArrayList<ExchangeRatePrinter>();

	private List<ExchangeRateParser> parsers = new ArrayList<ExchangeRateParser>();

	private int scale = -1;

	private RoundingMode roundingMode = RoundingMode.UNNECESSARY;

	private int targetUnitsExponent;

	public ExchangeRateFormatterBuilder() {
	}

	public ExchangeRateFormatterBuilder appendRate() {
		RatePrinterParser ratePrinterParser = new RatePrinterParser();
		return appendInternal(ratePrinterParser, ratePrinterParser);
	}

	public ExchangeRateFormatterBuilder appendLiteral(CharSequence literal) {
		if (literal == null || literal.length() == 0) {
			return this;
		}
		ExchangeRateLiteralPrinterParserAdapter literalPrinterParser = new ExchangeRateLiteralPrinterParserAdapter(
				new LiteralPrinterParser(literal.toString()));
		return appendInternal(literalPrinterParser, literalPrinterParser);
	}

	public ExchangeRateFormatterBuilder appendSourceCurrency() {
		return appendInternal(Singletons.SOURCE_CURRENCY, Singletons.SOURCE_CURRENCY);
	}

	public ExchangeRateFormatterBuilder appendTargetCurrency() {
		return appendInternal(Singletons.TARGET_CURRENCY, Singletons.TARGET_CURRENCY);
	}

	public ExchangeRateFormatterBuilder appendTargetUnitCount() {
		return appendInternal(Singletons.TARGET_UNIT_COUNT, Singletons.TARGET_UNIT_COUNT);
	}

	public ExchangeRateFormatterBuilder append(ExchangeRatePrinter printer, ExchangeRateParser parser) {
		return appendInternal(printer, parser);
	}

	public ExchangeRateFormatterBuilder setScale(int scale, RoundingMode roundingMode) {
		Utils.isTrue(scale >= 0, "Scale must be greater or equal to 0");
		Utils.notNull(roundingMode, "RoundingMode must not be null");
		this.scale = scale;
		this.roundingMode = roundingMode;
		return this;
	}

	public ExchangeRateFormatterBuilder setTargetUnitsExponent(int targetUnitsExponent) {
		Utils.isTrue(targetUnitsExponent >= 0, "targetUnitsExponent has to equal or greater than 0");
		this.targetUnitsExponent = targetUnitsExponent;
		return this;
	}

	private ExchangeRateFormatterBuilder appendInternal(ExchangeRatePrinter printer, ExchangeRateParser parser) {
		printers.add(printer);
		parsers.add(parser);
		return this;
	}

	public ExchangeRateFormatter toFormatter() {
		return toFormatter(Locale.getDefault());
	}

	public ExchangeRateFormatter toFormatter(Locale locale) {
		BaseFormatter.checkNotNull(locale, "Locale must not be null");
		ExchangeRatePrinter[] printersCopy = (ExchangeRatePrinter[]) printers.toArray(new ExchangeRatePrinter[printers
				.size()]);
		ExchangeRateParser[] parsersCopy = (ExchangeRateParser[]) parsers
				.toArray(new ExchangeRateParser[parsers.size()]);
		return new ExchangeRateFormatter(locale, printersCopy, parsersCopy, scale, roundingMode, targetUnitsExponent);
	}

	private static enum Singletons implements ExchangeRatePrinter, ExchangeRateParser {
		SOURCE_CURRENCY("${source}") {
			public void print(ExchangeRatePrintContext context, Appendable appendable, ExchangeRate exchangeRate)
					throws IOException {
				PrintParseUtils.printCurrency(context, appendable, exchangeRate.getSource(), CurrencyUnitFormat.CODE);
			}

			public void parse(final ExchangeRateParseContext context) {
				PrintParseUtils.parseCurrency(context, new ParseCallback() {

					public void parsed(String parsedText, int pos) {
						try {
							context.setSource(CurrencyUnit.of(parsedText));
							context.setIndex(pos);
						} catch (IllegalCurrencyException ex) {
							context.setError();
						}
					}
				});
			}
		},
		TARGET_CURRENCY("${target}") {
			public void print(ExchangeRatePrintContext context, Appendable appendable, ExchangeRate exchangeRate)
					throws IOException {
				PrintParseUtils.printCurrency(context, appendable, exchangeRate.getTarget(), CurrencyUnitFormat.CODE);
			}

			public void parse(final ExchangeRateParseContext context) {
				PrintParseUtils.parseCurrency(context, new ParseCallback() {

					public void parsed(String parsedText, int pos) {
						try {
							context.setTarget(CurrencyUnit.of(parsedText));
							context.setIndex(pos);
						} catch (IllegalCurrencyException ex) {
							context.setError();
						}
					}
				});
			}
		},
		TARGET_UNIT_COUNT("${targetUnitCount}") {
			public void print(ExchangeRatePrintContext context, Appendable appendable, ExchangeRate exchangeRate)
					throws IOException {
				double pow = Math.pow(10, context.getTargetUnitsExponent());
				appendable.append(Integer.toString((int) pow));
			}

			public void parse(final ExchangeRateParseContext context) {
				MoneyAmountStyle activeStyle = MoneyAmountStyle.LOCALIZED_NO_GROUPING.localize(context.getLocale());;
				PrintParseUtils.parseNumber(context, activeStyle, new ParseCallback() {
					
					public void parsed(String parsedText, int pos) {
						try {
							if (parsedText != null && parsedText.length() >0) {
								int targetUnitsCount = Integer.parseInt(parsedText);
								double exp = Math.log10(targetUnitsCount);
								long rounded = Math.round(exp);
								if (rounded != exp) {
									context.setError();
								} else {
									context.setTargetUnitCountExponent((int) rounded);
						            context.setIndex(pos);
								}	
							}
				        } catch (NumberFormatException ex) {
				            context.setError();
				        }
					}
				});
			}
		};

		private final String toString;

		Singletons(String toString) {
			this.toString = toString;
		}

		@Override
		public String toString() {
			return toString;
		}
	}
}
