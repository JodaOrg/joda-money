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
import java.math.BigDecimal;

import org.joda.money.BigMoney;

/**
 * Prints and parses using delegated formatters, one for positive and one for megative.
 * <p>
 * This class is immutable and thread-safe.
 */
final class SignedPrinterParser implements MoneyPrinter, MoneyParser, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** The formatter to use when positive. */
    private final MoneyFormatter whenPositive;
    /** The formatter to use when zero. */
    private final MoneyFormatter whenZero;
    /** The formatter to use when negative. */
    private final MoneyFormatter whenNegative;

    /**
     * Constructor.
     * @param whenPositive  the formatter to use when the amount is positive
     * @param whenZero  the formatter to use when the amount is zero
     * @param whenNegative  the formatter to use when the amount is positive
     */
    SignedPrinterParser(MoneyFormatter whenPositive, MoneyFormatter whenZero, MoneyFormatter whenNegative) {
        this.whenPositive = whenPositive;
        this.whenZero = whenZero;
        this.whenNegative = whenNegative;
    }

    //-----------------------------------------------------------------------
    @Override
    public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
        var fmt = (money.isZero() ? whenZero : money.isPositive() ? whenPositive : whenNegative);
        fmt.getPrinterParser().print(context, appendable, money);
    }

    @Override
    public void parse(MoneyParseContext context) {
        var positiveContext = tryParse(context, whenPositive);
        var zeroContext = tryParse(context, whenZero);
        var negativeContext = tryParse(context, whenNegative);
        
        var best = selectBestParseResult(positiveContext, zeroContext, negativeContext);
        
        if (best == null) {
            context.setError();
        } else {
            applyParseResult(context, best, zeroContext, negativeContext);
        }
    }

    /**
     * Attempts to parse using the given formatter.
     * 
     * @param context  the parent context
     * @param formatter  the formatter to use for parsing
     * @return the child context with parse results
     */
    private MoneyParseContext tryParse(MoneyParseContext context, MoneyFormatter formatter) {
        var childContext = context.createChild();
        formatter.getPrinterParser().parse(childContext);
        return childContext;
    }

    /**
     * Selects the best parse result from the three attempted parses.
     * The best result is the one that parsed the most characters (highest index).
     * 
     * @param positiveContext  the result from positive formatter
     * @param zeroContext  the result from zero formatter
     * @param negativeContext  the result from negative formatter
     * @return the best context, or null if all failed
     */
    private MoneyParseContext selectBestParseResult(
            MoneyParseContext positiveContext, 
            MoneyParseContext zeroContext, 
            MoneyParseContext negativeContext) {
        
        var best = (MoneyParseContext) null;
        
        if (!positiveContext.isError()) {
            best = positiveContext;
        }
        
        if (!zeroContext.isError()) {
            if (best == null || zeroContext.getIndex() > best.getIndex()) {
                best = zeroContext;
            }
        }
        
        if (!negativeContext.isError()) {
            if (best == null || negativeContext.getIndex() > best.getIndex()) {
                best = negativeContext;
            }
        }
        
        return best;
    }

    /**
     * Applies the best parse result to the parent context and adjusts the amount
     * based on which formatter was used.
     * 
     * @param context  the parent context to update
     * @param best  the best parse result
     * @param zeroContext  the zero formatter result (for comparison)
     * @param negativeContext  the negative formatter result (for comparison)
     */
    private void applyParseResult(
            MoneyParseContext context, 
            MoneyParseContext best, 
            MoneyParseContext zeroContext, 
            MoneyParseContext negativeContext) {
        
        context.mergeChild(best);
        
        if (best == zeroContext) {
            ensureAmountIsZero(context);
        } else if (best == negativeContext) {
            ensureAmountIsNegative(context);
        }
    }

    /**
     * Ensures the parsed amount is set to zero.
     * This is needed when the zero formatter is used, as the amount might be
     * parsed as a non-zero value.
     * 
     * @param context  the context to update
     */
    private void ensureAmountIsZero(MoneyParseContext context) {
        if (context.getAmount() == null || context.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            context.setAmount(BigDecimal.ZERO);
        }
    }

    /**
     * Ensures the parsed amount is negative.
     * This negates positive amounts when the negative formatter is used.
     * 
     * @param context  the context to update
     */
    private void ensureAmountIsNegative(MoneyParseContext context) {
        if (context.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            context.setAmount(context.getAmount().negate());
        }
    }

    @Override
    public String toString() {
        return "PositiveZeroNegative(" + whenPositive + "," + whenZero + "," + whenNegative + ")";
    }

}
