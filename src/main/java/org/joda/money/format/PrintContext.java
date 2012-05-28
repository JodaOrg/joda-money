package org.joda.money.format;

import java.util.Locale;

public class PrintContext {

	/**
	 * The locale to print using.
	 */
	protected Locale locale;
	
	PrintContext(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Gets the locale.
	 * 
	 * @return the locale, never null
	 */
	public Locale getLocale() {
	    return locale;
	}

	/**
	 * Sets the locale.
	 * 
	 * @param locale  the locale, not null
	 */
	public void setLocale(Locale locale) {
	    MoneyFormatter.checkNotNull(locale, "Locale must not be null");
	    this.locale = locale;
	}

}
