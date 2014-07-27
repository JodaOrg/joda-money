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

import org.joda.money.BigMoney;

/**
 * Prints and parses a literal.
 * <p>
 * This class is immutable and thread-safe.
 */
final class LiteralPrinterParser implements MoneyPrinter, MoneyParser, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Literal. */
    private final String literal;

    /**
     * Constructor.
     * @param literal  the literal text, not null
     */
    LiteralPrinterParser(String literal) {
        this.literal = literal;
    }

    //-----------------------------------------------------------------------
    @Override
    public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
        appendable.append(literal);
    }

    @Override
    public void parse(MoneyParseContext context) {
        int endPos = context.getIndex() + literal.length();
        if (endPos <= context.getTextLength() &&
                context.getTextSubstring(context.getIndex(), endPos).equals(literal)) {
            context.setIndex(endPos);
        } else {
            context.setError();
        }
    }

    @Override
    public String toString() {
        return "'" + literal + "'";
    }

}
