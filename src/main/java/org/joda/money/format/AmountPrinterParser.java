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
    @Override
    public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
        var activeStyle = style.localize(context.getLocale());
        String str;
        if (money.isNegative()) {
            if (!activeStyle.isAbsValue()) {
                appendable.append(activeStyle.getNegativeSignCharacter());
            }
            str = money.negated().getAmount().toPlainString();
        } else {
            str = money.getAmount().toPlainString();
        }
        var zeroChar = activeStyle.getZeroCharacter();
        if (zeroChar != '0') {
            var diff = zeroChar - '0';
            var zeroConvert = new StringBuilder(str);
            for (var i = 0; i < str.length(); i++) {
                var ch = str.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    zeroConvert.setCharAt(i, (char) (ch + diff));
                }
            }
            str = zeroConvert.toString();
        }
        var decPoint = str.indexOf('.');
        var afterDecPoint = decPoint + 1;
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
            var groupingSize = activeStyle.getGroupingSize();
            var extendedGroupingSize = activeStyle.getExtendedGroupingSize();
            extendedGroupingSize = extendedGroupingSize == 0 ? groupingSize : extendedGroupingSize;
            var groupingChar = activeStyle.getGroupingCharacter();
            var pre = (decPoint < 0 ? str.length() : decPoint);
            var post = (decPoint < 0 ? 0 : str.length() - decPoint - 1);
            appendable.append(str.charAt(0));
            for (var i = 1; i < pre; i++) {
                if (isPreGroupingPoint(pre - i, groupingSize, extendedGroupingSize)) {
                    appendable.append(groupingChar);
                }
                appendable.append(str.charAt(i));
            }
            if (decPoint >= 0 || activeStyle.isForcedDecimalPoint()) {
                appendable.append(activeStyle.getDecimalPointCharacter());
            }
            if (activeStyle.getGroupingStyle() == GroupingStyle.BEFORE_DECIMAL_POINT) {
                if (decPoint >= 0) {
                    appendable.append(str.substring(afterDecPoint));
                }
            } else {
                for (var i = 0; i < post; i++) {
                    appendable.append(str.charAt(i + afterDecPoint));
                    if (isPostGroupingPoint(i, post, groupingSize, extendedGroupingSize)) {
                        appendable.append(groupingChar);
                    }
                }
            }
        }
    }

    private boolean isPreGroupingPoint(int remaining, int groupingSize, int extendedGroupingSize) {
        if (remaining >= groupingSize + extendedGroupingSize) {
            return (remaining - groupingSize) % extendedGroupingSize == 0;
        }
        return remaining % groupingSize == 0;
    }

    private boolean isPostGroupingPoint(int i, int post, int groupingSize, int extendedGroupingSize) {
        var atEnd = (i + 1) >= post;
        if (i > groupingSize) {
            return (i - groupingSize) % extendedGroupingSize == (extendedGroupingSize - 1) && !atEnd;
        }
        return i % groupingSize == (groupingSize - 1) && !atEnd;
    }

    @Override
    public void parse(MoneyParseContext context) {
        var len = context.getTextLength();
        var activeStyle = style.localize(context.getLocale());
        var buf = new char[len - context.getIndex()];
        var bufPos = 0;
        var dpSeen = false;
        var pos = context.getIndex();
        if (pos < len) {
            var ch = context.getText().charAt(pos++);
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
        var lastWasGroup = false;
        for (; pos < len; pos++) {
            var ch = context.getText().charAt(pos);
            if (ch >= activeStyle.getZeroCharacter() && ch < activeStyle.getZeroCharacter() + 10) {
                buf[bufPos++] = (char) ('0' + ch - activeStyle.getZeroCharacter());
                lastWasGroup = false;
            } else if (ch == activeStyle.getDecimalPointCharacter() && !dpSeen) {
                buf[bufPos++] = '.';
                dpSeen = true;
                lastWasGroup = false;
            } else if (ch == activeStyle.getGroupingCharacter() && !lastWasGroup) {
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
