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
        MoneyFormatter fmt = (money.isZero() ? whenZero : money.isPositive() ? whenPositive : whenNegative);
        fmt.getPrinterParser().print(context, appendable, money);
    }

    @Override
    public void parse(MoneyParseContext context) {
        MoneyParseContext positiveContext = context.createChild();
        whenPositive.getPrinterParser().parse(positiveContext);
        MoneyParseContext zeroContext = context.createChild();
        whenZero.getPrinterParser().parse(zeroContext);
        MoneyParseContext negativeContext = context.createChild();
        whenNegative.getPrinterParser().parse(negativeContext);
        MoneyParseContext best = null;
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
        if (best == null) {
            context.setError();
        } else {
            context.mergeChild(best);
            if (best == zeroContext) {
                if (context.getAmount() == null || context.getAmount().compareTo(BigDecimal.ZERO) != 0) {
                    context.setAmount(BigDecimal.ZERO);
                }
            } else if (best == negativeContext && context.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                context.setAmount(context.getAmount().negate());
            }
        }
    }

    @Override
    public String toString() {
        return "PositiveZeroNegative(" + whenPositive + "," + whenZero + "," + whenNegative + ")";
    }

}
