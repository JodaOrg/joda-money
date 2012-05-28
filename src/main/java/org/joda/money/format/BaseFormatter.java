package org.joda.money.format;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

/**
 * Base class for formatter objects.
 * 
 * @author Tom Pasierb
 *
 * @param <PRINTER> type used for printing
 * @param <PARSER> type used for parsing
 */
abstract class BaseFormatter<PRINTER, PARSER> implements Serializable {

	private static final long serialVersionUID = 207376616541L;
	
	/**
	 * The locale to use.
	 */
	protected final Locale locale;
	/**
	 * The printers.
	 */
	protected final PRINTER[] printers;
	/**
	 * The parsers.
	 */
	protected final PARSER[] parsers;

	/**
	 * Validates that the object specified is not null.
	 *
	 * @param object  the object to check, null throws exception
	 * @param message  the message to use in the exception, not null
	 * @throws NullPointerException if the input value is null
	 */
	protected static void checkNotNull(Object object, String message) {
	    if (object == null) {
	        throw new NullPointerException(message);
	    }
	}
	
	/**
	 * Configures this instance. 
	 * 
	 * @param locale  locale to be used when formatting and printing.
	 * @param printers a collection of printer objects
	 * @param parsers a collection of parser objects
	 */
	protected BaseFormatter(Locale locale, PRINTER[] printers, PARSER[] parsers) {
		assert locale != null;
        assert printers != null;
        assert parsers != null;
        assert printers.length == parsers.length;
		this.locale = locale;
		this.printers = printers;
		this.parsers = parsers;
	}

	/**
	 * Gets the locale to use.
	 * 
	 * @return the locale, never null
	 */
	public Locale getLocale() {
	    return locale;
	}

	/**
	 * Checks whether this formatter can print.
	 * <p>
	 * If the formatter cannot print, an UnsupportedOperationException will
	 * be thrown from the print methods.
	 * 
	 * @return true if the formatter can print
	 */
	public boolean isPrinter() {
	    return Arrays.asList(printers).contains(null) == false;
	}

	/**
	 * Checks whether this formatter can parse.
	 * <p>
	 * If the formatter cannot parse, an UnsupportedOperationException will
	 * be thrown from the print methods.
	 * 
	 * @return true if the formatter can parse
	 */
	public boolean isParser() {
	    return Arrays.asList(parsers).contains(null) == false;
	}
	
	/**
	 * Gets a string summary of the formatter.
	 * 
	 * @return a string summarising the formatter, never null
	 */
	@Override
	public String toString() {
	    StringBuilder buf1 = new StringBuilder();
	    if (isPrinter()) {
	        for (PRINTER printer : printers) {
	            buf1.append(printer.toString());
	        }
	    }
	    StringBuilder buf2 = new StringBuilder();
	    if (isParser()) {
	        for (PARSER parser : parsers) {
	            buf2.append(parser.toString());
	        }
	    }
	    String str1 = buf1.toString();
	    String str2 = buf2.toString();
	    if (isPrinter() && !isParser()) {
	        return str1;
	    } else if (isParser() && !isPrinter()) {
	        return str2;
	    } else if (str1.equals(str2)) {
	        return str1;
	    } else {
	        return str1 + ":" + str2;
	    }
	}
	
}
