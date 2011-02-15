/*
 *  Copyright 2009-2011 Stephen Colebourne
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
 * Prints and parses the amount part of the money.
 * <p>
 * AmountPrinterParser is immutable and thread-safe.
 */
final class AmountPrinterParser implements MoneyPrinter, MoneyParser, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** The style to use. */
    private final MoneyAmountStyle iStyle;

    /**
     * Constructor.
     * @param style  the style, not null
     */
    AmountPrinterParser(MoneyAmountStyle style) {
        iStyle = style;
    }

    /** {@inheritDoc} */
    public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
        MoneyAmountStyle style = iStyle.localize(context.getLocale());
        String str = money.getAmount().toPlainString();
        char zeroChar = style.getZeroCharacter();
        if (zeroChar != '0') {
            int diff = zeroChar - '0';
            StringBuilder zeroConvert = new StringBuilder(str);
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    zeroConvert.setCharAt(i, (char) (ch + diff));
                }
            }
            str = zeroConvert.toString();
        }
        int decPoint = str.indexOf('.');
        if (style.isGrouping()) {
            int groupingSize = style.getGroupingSize();
            char groupingChar = style.getGroupingCharacter();
            int pre = (decPoint < 0 ? str.length() : decPoint);
            int post = (decPoint < 0 ? 0 : str.length() - decPoint - 1);
            for (int i = 0; pre > 0; i++, pre--) {
                appendable.append(str.charAt(i));
                if (pre > 3 && pre % groupingSize == 1) {
                    appendable.append(groupingChar);
                }
            }
            if (decPoint >= 0 || style.isForcedDecimalPoint()) {
                appendable.append(style.getDecimalPointCharacter());
            }
            decPoint++;
            for (int i = 0; i < post; i++) {
                appendable.append(str.charAt(i + decPoint));
                if (i % groupingSize == 2) {
                    appendable.append(groupingChar);
                }
            }
        } else {
            if (decPoint < 0) {
                appendable.append(str);
                if (style.isForcedDecimalPoint()) {
                    appendable.append(style.getDecimalPointCharacter());
                }
            } else {
                appendable.append(str.subSequence(0, decPoint))
                    .append(style.getDecimalPointCharacter()).append(str.substring(decPoint + 1));
            }
        }
    }

    /** {@inheritDoc} */
    public void parse(MoneyParseContext context) {
        final int len = context.getTextLength();
        final MoneyAmountStyle style = iStyle.localize(context.getLocale());
        char[] buf = new char[len - context.getIndex()];
        int bufPos = 0;
        boolean dpSeen = false;
        boolean lastWasGroup = false;
        int pos = context.getIndex();
        if (pos < len) {
            char ch = context.getText().charAt(pos++);
            if (ch == style.getNegativeSignCharacter()) {
                buf[bufPos++] = '-';
            } else if (ch == style.getPositiveSignCharacter()) {
                buf[bufPos++] = '+';
            } else if (ch >= style.getZeroCharacter() && ch < style.getZeroCharacter() + 10) {
                buf[bufPos++] = (char) ('0' + ch - style.getZeroCharacter());
            } else if (ch == style.getDecimalPointCharacter()) {
                buf[bufPos++] = '.';
                dpSeen = true;
            } else {
                context.setError();
                return;
            }
        }
        for (; pos < len; pos++) {
            char ch = context.getText().charAt(pos);
            if (ch >= style.getZeroCharacter() && ch < style.getZeroCharacter() + 10) {
                buf[bufPos++] = (char) ('0' + ch - style.getZeroCharacter());
                lastWasGroup = false;
            } else if (ch == style.getDecimalPointCharacter() && dpSeen == false) {
                buf[bufPos++] = '.';
                dpSeen = true;
                lastWasGroup = false;
            } else if (ch == style.getGroupingCharacter() && lastWasGroup == false) {
                lastWasGroup = true;
            } else {
                break;
            }
        }
        if (lastWasGroup) {
            pos--;
        }
        try {
            context.setAmount(new BigDecimal(buf, 0, bufPos));
            context.setIndex(pos);
        } catch (NumberFormatException ex) {
            throw new MoneyFormatException("Invalid amount", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "${amount}";
    }

}
