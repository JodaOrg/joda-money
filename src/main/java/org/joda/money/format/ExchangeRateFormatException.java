package org.joda.money.format;

import org.joda.money.ExchangeRate;

/**
 * An excpetion thrown whenever there is a problem with formatting or printing an {@link ExchangeRate}.
 * 
 * @author Tom Pasierb
 */
public class ExchangeRateFormatException extends RuntimeException {

	private static final long serialVersionUID = 90610821118L;

	/**
	 * A constructor taking a reason string.
	 * 
	 * @param message a text message that describes why the exception was thrown
	 */
	public ExchangeRateFormatException(String message) {
		super(message);
	}

	/**
	 * A constructor taking a reason and a cause. 
	 * 
	 * @param message a text message that describes why the exception was thrown
	 * @param cause an exception that was the cause of this exception
	 */
	public ExchangeRateFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
