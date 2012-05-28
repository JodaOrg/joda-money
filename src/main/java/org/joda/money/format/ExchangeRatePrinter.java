package org.joda.money.format;

import java.io.IOException;

import org.joda.money.ExchangeRate;

public interface ExchangeRatePrinter {

	void print(ExchangeRatePrintContext context, Appendable appendable, ExchangeRate exchangeRate) throws IOException;
	
}
