package org.joda.money.format;

import java.io.IOException;
import java.io.Serializable;

import org.joda.money.ExchangeRate;

class ExchangeRateLiteralPrinterParserAdapter implements ExchangeRatePrinter, ExchangeRateParser, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final LiteralPrinterParser literalPrinterParser;
	
	ExchangeRateLiteralPrinterParserAdapter(LiteralPrinterParser literalPrinterParser) {
		this.literalPrinterParser = literalPrinterParser;
	}

	public void print(ExchangeRatePrintContext context, Appendable appendable, ExchangeRate exchangeRate)
			throws IOException {
		literalPrinterParser.print(context, appendable);
	}

	public void parse(ExchangeRateParseContext context) {
		literalPrinterParser.parse(context);
	}

	@Override
	public String toString() {
		return literalPrinterParser.toString();
	}

	
}
