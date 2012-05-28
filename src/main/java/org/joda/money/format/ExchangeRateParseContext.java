package org.joda.money.format;

import java.math.BigDecimal;
import java.util.Locale;

import org.joda.money.CurrencyUnit;
import org.joda.money.ExchangeRate;

public final class ExchangeRateParseContext extends ParseContext {

	private CurrencyUnit target;
	private CurrencyUnit source;
	private BigDecimal rate;
	private int targetUnitCountExponent = 0;

	ExchangeRateParseContext(Locale locale, CharSequence text, int textIndex) {
		super(locale, text, textIndex);
	}

	public void setTarget(CurrencyUnit target) {
		this.target = target;
	}

	public void setSource(CurrencyUnit source) {
		this.source = source;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	public void setTargetUnitCountExponent(int targetUnitCountExponent) {
		this.targetUnitCountExponent = targetUnitCountExponent;
	}

	@Override
	protected boolean isComplete() {
		return target != null && source != null && rate != null;
	}

	public ExchangeRate toExchangeRate() {
		if (target == null) {
			throw new ExchangeRateFormatException("Cannot convert to ExchangeRate as no target currency found");
		}
		if (source == null) {
			throw new ExchangeRateFormatException("Cannot convert to ExchangeRate as no source currency found");
		}
		if (rate == null) {
			throw new ExchangeRateFormatException("Cannot convert to ExchangeRate as no rate found");
		}
		
		BigDecimal rate = this.rate;
		if (targetUnitCountExponent > 0) {
			rate = rate.movePointLeft(targetUnitCountExponent);
		} else if (targetUnitCountExponent < 0) {
			rate = rate.movePointRight(targetUnitCountExponent);
		}
		
		return ExchangeRate.of(rate, source, target);
	}

}
