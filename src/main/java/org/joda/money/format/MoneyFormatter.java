/*
 *  Copyright 2009-present, Stephen Colebourne
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
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.BigMoneyProvider;
import org.joda.money.Money;

/**
 * Formats instances of money to and from a String.
 * <p>
 * Instances of {@code MoneyFormatter} can be created by
 * {@code MoneyFormatterBuilder}.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class MoneyFormatter implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 2385346258L;

    /**
     * The locale to use.
     */
    private final Locale locale;
    /**
     * The printer/parser.
     */
    private final MultiPrinterParser printerParser;

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
    MoneyFormatter(Locale locale, MoneyPrinter[] printers, MoneyParser[] parsers) {
        assert locale != null;
        assert printers != null;
        assert parsers != null;
        assert printers.length == parsers.length;
        this.locale = locale;
        this.printerParser = new MultiPrinterParser(printers, parsers);
    }

    /**
     * Constructor, creating a new formatter.
     * 
     * @param locale  the locale to use, not null
     * @param printerParser  the printer/parser, not null
     */
    private MoneyFormatter(Locale locale, MultiPrinterParser printerParser) {
        assert locale != null;
        assert printerParser != null;
        this.locale = locale;
        this.printerParser = printerParser;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the printer/parser.
     * 
     * @return the printer/parser, never null
     */
    MultiPrinterParser getPrinterParser() {
        return printerParser;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale to use.
     * 
     * @return the locale, never null
     */
    public Locale getLocale() {
        return locale;
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
        return new MoneyFormatter(locale, printerParser);
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
        return printerParser.isPrinter();
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
        return printerParser.isParser();
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
    public String print(BigMoneyProvider moneyProvider) {
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
    public void print(Appendable appendable, BigMoneyProvider moneyProvider) {
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
    public void printIO(Appendable appendable, BigMoneyProvider moneyProvider) throws IOException {
        checkNotNull(moneyProvider, "BigMoneyProvider must not be null");
        if (isPrinter() == false) {
            throw new UnsupportedOperationException("MoneyFomatter has not been configured to be able to print");
        }
        
        BigMoney money = BigMoney.of(moneyProvider);
        MoneyPrintContext context = new MoneyPrintContext(locale);
        printerParser.print(context, appendable, money);
    }

    //-----------------------------------------------------------------------
    /**
     * Fully parses the text into a {@code BigMoney}.
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
    public BigMoney parseBigMoney(CharSequence text) {
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
        return result.toBigMoney();
    }

    /**
     * Fully parses the text into a {@code Money} requiring that the parsed
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
    public Money parseMoney(CharSequence text) {
        return parseBigMoney(text).toMoney();
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
        MoneyParseContext context = new MoneyParseContext(locale, text, startIndex);
        printerParser.parse(context);
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
        return printerParser.toString();
    }

}
