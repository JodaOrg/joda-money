/*
 *  Copyright 2009 Stephen Colebourne
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
import java.util.Locale;

import org.joda.money.Money;

/**
 * Formats instances of Money to and from a String.
 * <p>
 * Instances of <code>MoneyFormatter</code> can be created by
 * <code>MoneyFormatterBuilder</code>.
 * <p>
 * MoneyFormatter is immutable and thread-safe.
 */
public final class MoneyFormatter {

    /**
     * The locale to use.
     */
    private final Locale iLocale;
    /**
     * The printers.
     */
    private final MoneyPrinter[] iPrinters;

    //-----------------------------------------------------------------------
    /**
     * Validates that the object specified is not null
     *
     * @param object  the object to check, not null
     * @throws NullPointerException if the input value is null
     */
    static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new monetary instance.
     * 
     * @param locale  the locale to use, not null
     * @param printers  the printers, not null
     */
    MoneyFormatter(
            Locale locale,
            MoneyPrinter[] printers) {
        iLocale = locale;
        iPrinters = printers;
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
        return new MoneyFormatter(locale, iPrinters);
    }

    //-----------------------------------------------------------------------
    /**
     * Prints a <code>Money</code> instance to a <code>String</code>.
     * 
     * @param money  the money to print, not null
     * @return the string printed using the settings of this formatter
     * @throws MoneyFormatException if there is a problem while printing
     */
    public String print(Money money) {
        StringBuilder buf = new StringBuilder();
        print(buf, money);
        return buf.toString();
    }

    /**
     * Prints a <code>Money</code> instance to an <code>Appendable</code> converting
     * any <code>IOException</code> to a <code>MoneyFormatException</code>.
     * <p>
     * Example implementations of <code>Appendable</code> are <code>StringBuilder</code>,
     * <code>StringBuffer</code> or <code>Writer</code>. Note that <code>StringBuilder</code>
     * and <code>StringBuffer</code> never throw an <code>IOException</code>.
     * 
     * @param appendable  the appendable to add to, not null
     * @param money  the money to print, not null
     * @return the string printed using the settings of this formatter
     * @throws MoneyFormatException if there is a problem while printing
     */
    public void print(Appendable appendable, Money money) {
        try {
            printIO(appendable, money);
        } catch (IOException ex) {
            throw new MoneyFormatException(ex.getMessage(), ex);
        }
    }

    /**
     * Prints a <code>Money</code> instance to an <code>Appendable</code> potentially
     * throwing an <code>IOException</code>.
     * <p>
     * Example implementations of <code>Appendable</code> are <code>StringBuilder</code>,
     * <code>StringBuffer</code> or <code>Writer</code>. Note that <code>StringBuilder</code>
     * and <code>StringBuffer</code> never throw an <code>IOException</code>.
     * 
     * @param appendable  the appendable to add to, not null
     * @param money  the money to print, not null
     * @return the string printed using the settings of this formatter
     * @throws MoneyFormatException if there is a problem while printing
     * @throws IOException if an IO error occurs
     */
    public void printIO(Appendable appendable, Money money) throws IOException {
        checkNotNull(money, "Money must not be null");
        MoneyPrintContext context = new MoneyPrintContext(iLocale);
        for (MoneyPrinter printer : iPrinters) {
            printer.print(context, appendable, money);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a string summary of the formatter.
     * 
     * @return a string summarising the formatter, never null
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (MoneyPrinter printer : iPrinters) {
            buf.append(printer.toString());
        }
        return buf.toString();
    }

}
