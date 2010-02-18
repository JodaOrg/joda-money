/*
 *  Copyright 2009-2010 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.money.format;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

import org.joda.money.Money;
import org.joda.money.MoneyProvider;
import org.joda.money.StandardMoney;

/**
 * Formats instances of money to and from a String.
 * <p>
 * Instances of {@code MoneyFormatter} can be created by
 * {@code MoneyFormatterBuilder}.
 * <p>
 * MoneyFormatter is immutable and thread-safe.
 */
public final class MoneyFormatter implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 2385346258L;

    /**
     * The locale to use.
     */
    private final Locale iLocale;
    /**
     * The printers.
     */
    private final MoneyPrinter[] iPrinters;
    /**
     * The parsers.
     */
    private final MoneyParser[] iParsers;

    //-----------------------------------------------------------------------
    /**
     * Validates that the object specified is not null
     *
     * @param object  the object to check, null throws exception
     * @param message  the message to use in the exception, not null
     * @throws NullPointerException if the input value is null
     */
    static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new formatter.
     * 
     * @param locale  the locale to use, not null
     * @param printers  the printers, not null
     * @param parsers  the parsers, not null
     */
    MoneyFormatter(
            Locale locale,
            MoneyPrinter[] printers,
            MoneyParser[] parsers) {
        assert locale != null;
        assert printers != null;
        assert parsers != null;
        assert printers.length == parsers.length;
        iLocale = locale;
        iPrinters = printers;
        iParsers = parsers;
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the printers and parsers from this formatter to the builder.
     * 
     * @param builder  the builder to append to not null
     */
    void appendTo(MoneyFormatterBuilder builder) {
        for (int i = 0; i < iPrinters.length; i++) {
            builder.append(iPrinters[i], iParsers[i]);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to use.
     * 
     * @return the locale, never null
     */
    public Locale getLocale() {
        return iLocale;
    }

    /**
     * Returns a copy of this instance with the specified locale.
     * <p>
     * Changing the locale may change the style of output depending on how the
     * formatter has been configured.
     * 
     * @param locale  the locale, not null
     * @return the new instance, never null
     */
    public MoneyFormatter withLocale(Locale locale) {
        checkNotNull(locale, "Locale must not be null");
        return new MoneyFormatter(locale, iPrinters, iParsers);
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether this formatter can print.
     * <p>
     * If the formatter cannot print, an UnsupportedOperationException will
     * be thrown from the print methods.
     * 
     * @return true if the formatter can print
     */
    public boolean isPrinter() {
        return Arrays.asList(iPrinters).contains(null) == false;
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
        return Arrays.asList(iParsers).contains(null) == false;
    }

    //-----------------------------------------------------------------------
    /**
     * Prints a monetary value to a {@code String}.
     * 
     * @param moneyProvider  the money to print, not null
     * @return the string printed using the settings of this formatter
     * @throws UnsupportedOperationException if the formatter is unable to print
     * @throws MoneyFormatException if there is a problem while printing
     */
    public String print(MoneyProvider moneyProvider) {
        StringBuilder buf = new StringBuilder();
        print(buf, moneyProvider);
        return buf.toString();
    }

    /**
     * Prints a monetary value to an {@code Appendable} converting
     * any {@code IOException} to a {@code MoneyFormatException}.
     * <p>
     * Example implementations of {@code Appendable} are {@code StringBuilder},
     * {@code StringBuffer} or {@code Writer}. Note that {@code StringBuilder}
     * and {@code StringBuffer} never throw an {@code IOException}.
     * 
     * @param appendable  the appendable to add to, not null
     * @param moneyProvider  the money to print, not null
     * @throws UnsupportedOperationException if the formatter is unable to print
     * @throws MoneyFormatException if there is a problem while printing
     */
    public void print(Appendable appendable, MoneyProvider moneyProvider) {
        try {
            printIO(appendable, moneyProvider);
        } catch (IOException ex) {
            throw new MoneyFormatException(ex.getMessage(), ex);
        }
    }

    /**
     * Prints a monetary value to an {@code Appendable} potentially
     * throwing an {@code IOException}.
     * <p>
     * Example implementations of {@code Appendable} are {@code StringBuilder},
     * {@code StringBuffer} or {@code Writer}. Note that {@code StringBuilder}
     * and {@code StringBuffer} never throw an {@code IOException}.
     * 
     * @param appendable  the appendable to add to, not null
     * @param moneyProvider  the money to print, not null
     * @throws UnsupportedOperationException if the formatter is unable to print
     * @throws MoneyFormatException if there is a problem while printing
     * @throws IOException if an IO error occurs
     */
    public void printIO(Appendable appendable, MoneyProvider moneyProvider) throws IOException {
        checkNotNull(moneyProvider, "MoneyProvider must not be null");
        if (isPrinter() == false) {
            throw new UnsupportedOperationException("MoneyFomatter has not been configured to be able to print");
        }
        
        Money money = Money.from(moneyProvider);
        MoneyPrintContext context = new MoneyPrintContext(iLocale);
        for (MoneyPrinter printer : iPrinters) {
            printer.print(context, appendable, money);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Fully parses the text into a {@code Money}.
     * <p>
     * The parse must complete normally and parse the entire text (currency and amount).
     * If the parse completes without reading the entire length of the text, an exception is thrown.
     * If any other problem occurs during parsing, an exception is thrown.
     * 
     * @param text  the text to parse, not null
     * @return the parsed monetary value, never null
     * @throws UnsupportedOperationException if the formatter is unable to parse
     * @throws MoneyFormatException if there is a problem while parsing
     */
    public Money parseMoney(CharSequence text) {
        checkNotNull(text, "Text must not be null");
        MoneyParseContext result = parse(text, 0);
        if (result.isError() || result.isFullyParsed() == false || result.isComplete() == false) {
            String str = (text.length() > 64 ? text.subSequence(0, 64).toString() + "..." : text.toString());
            if (result.isError()) {
                throw new MoneyFormatException("Text could not be parsed at index " + result.getErrorIndex() + ": " + str);
            } else if (result.isFullyParsed() == false) {
                throw new MoneyFormatException("Unparsed text found at index " + result.getIndex() + ": " + str);
            } else {
                throw new MoneyFormatException("Parsing did not find both currency and amount: " + str);
            }
        }
        return result.toMoney();
    }

    /**
     * Fully parses the text into a {@code StandardMoney} requiring that the parsed
     * amount has the correct number of decimal places.
     * <p>
     * The parse must complete normally and parse the entire text (currency and amount).
     * If the parse completes without reading the entire length of the text, an exception is thrown.
     * If any other problem occurs during parsing, an exception is thrown.
     * 
     * @param text  the text to parse, not null
     * @return the parsed monetary value, never null
     * @throws UnsupportedOperationException if the formatter is unable to parse
     * @throws MoneyFormatException if there is a problem while parsing
     * @throws ArithmeticException if the scale of the parsed money exceeds the scale of the currency
     */
    public StandardMoney parseStandardMoney(CharSequence text) {
        return parseMoney(text).toStandardMoney();
    }

    /**
     * Parses the text extracting monetary information.
     * <p>
     * This method parses the input providing low-level access to the parsing state.
     * The resulting context contains the parsed text, indicator of error, position
     * following the parse and the parsed currency and amount.
     * Together, these provide enough information for higher level APIs to use.
     *
     * @param text  the text to parse, not null
     * @param startIndex  the start index to parse from
     * @return the parsed monetary value, null only if the parse results in an error
     * @throws IndexOutOfBoundsException if the start index is invalid
     * @throws UnsupportedOperationException if this formatter cannot parse
     */
    public MoneyParseContext parse(CharSequence text, int startIndex) {
        checkNotNull(text, "Text must not be null");
        if (startIndex < 0 || startIndex > text.length()) {
            throw new StringIndexOutOfBoundsException("Invalid start index: " + startIndex);
        }
        if (isParser() == false) {
            throw new UnsupportedOperationException("MoneyFomatter has not been configured to be able to parse");
        }
        MoneyParseContext context = new MoneyParseContext(iLocale, text, startIndex);
        for (MoneyParser parser : iParsers) {
            parser.parse(context);
            if (context.isError()) {
                break;
            }
        }
        return context;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a string summary of the formatter.
     * 
     * @return a string summarising the formatter, never null
     */
    @Override
    public String toString() {
        StringBuilder buf1 = new StringBuilder();
        if (isPrinter()) {
            for (MoneyPrinter printer : iPrinters) {
                buf1.append(printer.toString());
            }
        }
        StringBuilder buf2 = new StringBuilder();
        if (isParser()) {
            for (MoneyParser parser : iParsers) {
                buf2.append(parser.toString());
            }
        }
        String str1 = buf1.toString();
        String str2 = buf2.toString();
        if (isPrinter() && isParser() == false) {
            return str1;
        } else if (isParser() && isPrinter() == false) {
            return str2;
        } else if (str1.equals(str2)) {
            return str1;
        } else {
            return str1 + ":" + str2;
        }
    }

}
