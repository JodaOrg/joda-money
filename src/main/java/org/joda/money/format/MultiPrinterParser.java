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
import java.util.Arrays;

import org.joda.money.BigMoney;

/**
 * Prints and parses multiple printers/parsers.
 * <p>
 * This class is immutable and thread-safe.
 */
final class MultiPrinterParser implements MoneyPrinter, MoneyParser, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /**
     * The printers.
     */
    private final MoneyPrinter[] printers;
    /**
     * The parsers.
     */
    private final MoneyParser[] parsers;

    /**
     * Constructor.
     * @param printers  the printers, not null
     */
    MultiPrinterParser(MoneyPrinter[] printers, MoneyParser[] parsers) {
        this.printers = printers;
        this.parsers = parsers;
    }

    //-----------------------------------------------------------------------
    boolean isPrinter() {
        return Arrays.asList(printers).contains(null) == false;
    }

    boolean isParser() {
        return Arrays.asList(parsers).contains(null) == false;
    }

    void appendTo(MoneyFormatterBuilder builder) {
        for (int i = 0; i < printers.length; i++) {
            builder.append(printers[i], parsers[i]);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
        for (MoneyPrinter printer : printers) {
            printer.print(context, appendable, money);
        }
    }

    @Override
    public void parse(MoneyParseContext context) {
        for (MoneyParser parser : parsers) {
            parser.parse(context);
            if (context.isError()) {
                break;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buf1 = new StringBuilder();
        if (isPrinter()) {
            for (MoneyPrinter printer : printers) {
                buf1.append(printer.toString());
            }
        }
        StringBuilder buf2 = new StringBuilder();
        if (isParser()) {
            for (MoneyParser parser : parsers) {
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
