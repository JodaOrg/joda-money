package org.joda.money.format;

import java.util.Locale;

public final class ExchangeRatePrintContext extends PrintContext {
	
	private final int targetUnitsExponent;

	ExchangeRatePrintContext(Locale locale, int targetUnitsExponent) {
		super(locale);
		assert targetUnitsExponent >= 0;
		this.targetUnitsExponent  = targetUnitsExponent;
	}
	
	public int getTargetUnitsExponent() {
		return targetUnitsExponent;
	}
	
}
