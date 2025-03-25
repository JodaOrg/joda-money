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

    private static final long serialVersionUID = 1L;

    private final MoneyAmountStyle style;


    private static final int MAX_DIGITS = 10;

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
                var currentChar = str.charAt(i);
                if (currentChar >= '0' && currentChar <= '9') {
                    zeroConvert.setCharAt(i, (char) (currentChar + diff));
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

        var zeroChar = activeStyle.getZeroCharacter(); // Introduced variable
        var digitRangeLimit = zeroChar + MAX_DIGITS;   // Introduce explaining variable

        if (pos < len) {
            var currentChar = context.getText().charAt(pos++); // Renamed
            if (currentChar == activeStyle.getNegativeSignCharacter()) {
                buf[bufPos++] = '-';
            } else if (currentChar == activeStyle.getPositiveSignCharacter()) {
                buf[bufPos++] = '+';
            } else if (isDigit(currentChar, zeroChar, digitRangeLimit)) {
                buf[bufPos++] = convertToStandardDigit(currentChar, zeroChar);
            } else if (currentChar == activeStyle.getDecimalPointCharacter()) {
                buf[bufPos++] = '.';
                dpSeen = true;
            } else {
                context.setError();
                return;
            }
        }

        var lastWasGroup = false;
        for (; pos < len; pos++) {
            var currentChar = context.getText().charAt(pos);
            if (isDigit(currentChar, zeroChar, digitRangeLimit)) {
                buf[bufPos++] = convertToStandardDigit(currentChar, zeroChar);
                lastWasGroup = false;
            } else if (currentChar == activeStyle.getDecimalPointCharacter() && !dpSeen) {
                buf[bufPos++] = '.';
                dpSeen = true;
                lastWasGroup = false;
            } else if (currentChar == activeStyle.getGroupingCharacter() && !lastWasGroup) {
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


    private boolean isDigit(char ch, char zeroChar, int digitRangeLimit) {
        return ch >= zeroChar && ch < digitRangeLimit;
    }


    private char convertToStandardDigit(char ch, char zeroChar) {
        return (char) ('0' + ch - zeroChar);
    }

    @Override
    public String toString() {
        return "${amount}";
    }
}
