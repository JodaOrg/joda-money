package org.joda.money.format;

import java.io.IOException;
import java.math.BigDecimal;

import org.joda.money.ExchangeRate;
import org.joda.money.format.PrintParseUtils.ParseCallback;

public class RatePrinterParser implements ExchangeRatePrinter, ExchangeRateParser {

	// TODO this should be generalized, or a dedicated instance should be created
	private MoneyAmountStyle style;
	
	public RatePrinterParser() {
		// TODO allow for customization
		this.style = MoneyAmountStyle.LOCALIZED_NO_GROUPING;
	}
	
	public void print(ExchangeRatePrintContext context, Appendable appendable, ExchangeRate exchangeRate)
			throws IOException {
		MoneyAmountStyle activeStyle = style.localize(context.getLocale());
		String str = exchangeRate.getRate().toPlainString();
		PrintParseUtils.printNumber(context, activeStyle, appendable, str);
	}
	
	public void parse(final ExchangeRateParseContext context) {
		MoneyAmountStyle activeStyle = style.localize(context.getLocale());
		PrintParseUtils.parseNumber(context, activeStyle, new ParseCallback() {
			
			public void parsed(String parsedText, int pos) {
				try {
		            context.setRate(new BigDecimal(parsedText));
		            context.setIndex(pos);
		        } catch (NumberFormatException ex) {
		            context.setError();
		        }
			}
		});
	}

	@Override
	public String toString() {
		return "${rate}";
	}
	
}
