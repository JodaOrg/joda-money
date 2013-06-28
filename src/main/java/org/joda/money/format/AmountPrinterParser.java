/*
 *  Copyright 2009-2013 Stephen Colebourne
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
import org.joda.money.MoneyUtils;

/**
 * Prints and parses the amount part of the money.
 * <p>
 * This class is immutable and thread-safe.
 */
final class AmountPrinterParser implements MoneyPrinter, MoneyParser, Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** The style to use. */
    private final MoneyAmountStyle style;

    /**
     * Constructor.
     * @param style  the style, not null
     */
    AmountPrinterParser(MoneyAmountStyle style) {
        this.style = style;
    }

    //-----------------------------------------------------------------------
    public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
        MoneyAmountStyle activeStyle = style.localize(context.getLocale());
        if (MoneyUtils.isNegative(money)) {
            money = money.negated();
            appendable.append(activeStyle.getNegativeSignCharacter());
        }
        String str = money.getAmount().toPlainString();
        char zeroChar = activeStyle.getZeroCharacter();
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
        final int decPoint = str.indexOf('.');
        final int afterDecPoint = decPoint + 1;;
        if (activeStyle.getGroupingStyle() == GroupingStyle.NONE) {
            if (decPoint < 0) {
                appendable.append(str);
                if (activeStyle.isForcedDecimalPoint()) {
                    appendable.append(activeStyle.getDecimalPointCharacter());
                }
            } else {
                appendable.append(str.subSequence(0, decPoint))
                    .append(activeStyle.getDecimalPointCharacter()).append(str.substring(afterDecPoint));
            }
        } else {
            int groupingSize = activeStyle.getGroupingSize();
            char groupingChar = activeStyle.getGroupingCharacter();
            int pre = (decPoint < 0 ? str.length() : decPoint);
            int post = (decPoint < 0 ? 0 : str.length() - decPoint - 1);
            for (int i = 0; pre > 0; i++, pre--) {
                appendable.append(str.charAt(i));
                if (pre > 3 && pre % groupingSize == 1) {
                    appendable.append(groupingChar);
                }
            }
            if (decPoint >= 0 || activeStyle.isForcedDecimalPoint()) {
                appendable.append(activeStyle.getDecimalPointCharacter());
            }
            if (activeStyle.getGroupingStyle() == GroupingStyle.BEFORE_DECIMAL_POINT) {
                appendable.append(str.substring(afterDecPoint));
            } else {
                for (int i = 0; i < post; i++) {
                    appendable.append(str.charAt(i + afterDecPoint));
                    if (i % groupingSize == 2) {
                        appendable.append(groupingChar);
                    }
                }
            }
        }
    }

    public void parse(MoneyParseContext context) {
        final int len = context.getTextLength();
        final MoneyAmountStyle activeStyle = style.localize(context.getLocale());
        char[] buf = new char[len - context.getIndex()];
        int bufPos = 0;
        boolean dpSeen = false;
        boolean lastWasGroup = false;
        int pos = context.getIndex();
        if (pos < len) {
            char ch = context.getText().charAt(pos++);
            if (ch == activeStyle.getNegativeSignCharacter()) {
                buf[bufPos++] = '-';
            } else if (ch == activeStyle.getPositiveSignCharacter()) {
                buf[bufPos++] = '+';
            } else if (ch >= activeStyle.getZeroCharacter() && ch < activeStyle.getZeroCharacter() + 10) {
                buf[bufPos++] = (char) ('0' + ch - activeStyle.getZeroCharacter());
            } else if (ch == activeStyle.getDecimalPointCharacter()) {
                buf[bufPos++] = '.';
                dpSeen = true;
            } else {
                context.setError();
                return;
            }
        }
        for ( ; pos < len; pos++) {
            char ch = context.getText().charAt(pos);
            if (ch >= activeStyle.getZeroCharacter() && ch < activeStyle.getZeroCharacter() + 10) {
                buf[bufPos++] = (char) ('0' + ch - activeStyle.getZeroCharacter());
                lastWasGroup = false;
            } else if (ch == activeStyle.getDecimalPointCharacter() && dpSeen == false) {
                buf[bufPos++] = '.';
                dpSeen = true;
                lastWasGroup = false;
            } else if (ch == activeStyle.getGroupingCharacter() && lastWasGroup == false) {
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
            context.setError();
        }
    }

    @Override
    public String toString() {
        return "${amount}";
    }

}
