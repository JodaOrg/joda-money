package org.joda.money.format;

import java.io.IOException;
import java.io.Serializable;

import org.joda.money.BigMoney;

class MoneyLiteralPrinterParserAdapter implements MoneyPrinter, MoneyParser, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private LiteralPrinterParser literalPrinterParser;

	MoneyLiteralPrinterParserAdapter(LiteralPrinterParser literalPrinterParser) {
		this.literalPrinterParser = literalPrinterParser;
	}

	public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
		literalPrinterParser.print(context, appendable);
	}

	public void parse(MoneyParseContext context) {
		literalPrinterParser.parse(context);
	}

	public String toString() {
		return literalPrinterParser.toString();
	}

}
