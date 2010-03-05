/*
 *  Copyright 2009-2010 Stephen Colebourne
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;

/**
 * Provides the ability to build a formatter for monetary values.
 * <p>
 * MoneyFormatter is a mutable builder - a new instance should be created for each use.
 * The formatters produced by the builder are immutable and thread-safe.
 */
public final class MoneyFormatterBuilder {

    /**
     * The printers.
     */
    private final List<MoneyPrinter> iPrinters = new ArrayList<MoneyPrinter>();
    /**
     * The parsers.
     */
    private final List<MoneyParser> iParsers = new ArrayList<MoneyParser>();

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new empty builder.
     */
    public MoneyFormatterBuilder() {
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the amount to the builder using a localized format.
     * <p>
     * The amount is the value itself, such as '12.34'.
     * 
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendAmount() {
        Amount pp = new Amount(MoneyAmountStyle.LOCALIZED_GROUPING);
        return appendInternal(pp, pp);
    }

    /**
     * Appends the amount to the builder using the specified amount style.
     * <p>
     * The amount is the value itself, such as '12.34'.
     * <p>
     * The amount style allows the formatting of the number to be controlled in detail.
     * See {@link MoneyAmountStyle} for more details.
     * 
     * @param style  the style to use, not null
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendAmount(MoneyAmountStyle style) {
        MoneyFormatter.checkNotNull(style, "MoneyAmountStyle must not be null");
        Amount pp = new Amount(style);
        return appendInternal(pp, pp);
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the currency code to the builder.
     * <p>
     * The currency code is the three letter ISO code, such as 'GBP'.
     * 
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendCurrencyCode() {
        return appendInternal(Singletons.CODE, Singletons.CODE);
    }

    /**
     * Appends the currency code to the builder.
     * <p>
     * The numeric code is the ISO numeric code, such as '826' and is
     * zero padded to three digits.
     * 
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendCurrencyNumeric3Code() {
        return appendInternal(Singletons.NUMERIC_3_CODE, Singletons.NUMERIC_3_CODE);
    }

    /**
     * Appends the currency code to the builder.
     * <p>
     * The numeric code is the ISO numeric code, such as '826'.
     * 
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendCurrencyNumericCode() {
        return appendInternal(Singletons.NUMERIC_CODE, Singletons.NUMERIC_CODE);
    }

    /**
     * Appends the localized currency symbol to the builder.
     * <p>
     * The localized currency symbol is the symbol as chosen by the locale
     * of the formatter.
     * <p>
     * Symbols cannot be parsed.
     * 
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendCurrencySymbolLocalized() {
        return appendInternal(Singletons.LOCALIZED_SYMBOL, null);
    }

    /**
     * Appends a literal to the builder.
     * <p>
     * The localized currency symbol is the symbol as chosen by the locale
     * of the formatter.
     * 
     * @param literal  the literal to append, null or empty ignored
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendLiteral(CharSequence literal) {
        if (literal == null || literal.length() == 0) {
            return this;
        }
        Literal pp = new Literal(literal.toString());
        return appendInternal(pp, pp);
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the printers and parsers from the specified formatter to this builder.
     * <p>
     * If the specified formatter cannot print, then the the output of this
     * builder will be unable to print. If the specified formatter cannot parse,
     * then the output of this builder will be unable to parse.
     * 
     * @param formatter  the formatter to append, not null
     * @return this for chaining, never null
     */
    public MoneyFormatterBuilder append(MoneyFormatter formatter) {
        MoneyFormatter.checkNotNull(formatter, "MoneyFormatter must not be null");
        formatter.appendTo(this);
        return this;
    }

    /**
     * Appends the specified printer and parser to this builder.
     * <p>
     * If null is specified then the formatter will be unable to print/parse.
     * 
     * @param printer  the printer to append, null makes the formatter unable to print
     * @param parser  the parser to append, null makes the formatter unable to parse
     * @return this for chaining, never null
     */
    public MoneyFormatterBuilder append(MoneyPrinter printer, MoneyParser parser) {
        return appendInternal(printer, parser);
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the specified printer and parser to this builder.
     * <p>
     * Either the printer or parser must be non-null.
     * 
     * @param printer  the printer to append, null makes the formatter unable to print
     * @param parser  the parser to append, null makes the formatter unable to parse
     * @return this for chaining, never null
     */
    private MoneyFormatterBuilder appendInternal(MoneyPrinter printer, MoneyParser parser) {
        iPrinters.add(printer);
        iParsers.add(parser);
        return this;
    }

    //-----------------------------------------------------------------------
    /**
     * Builds the formatter from the builder using the default locale.
     * <p>
     * Once the builder is in the correct state it must be converted to a
     * {@code MoneyFormatter} to be used. Calling this method does not
     * change the state of this instance, so it can still be used.
     * <p>
     * This method uses the default locale within the returned formatter.
     * It can be changed by calling {@link MoneyFormatter#withLocale(Locale)}.
     * 
     * @return the formatter built from this builder, never null
     */
    public MoneyFormatter toFormatter() {
        return toFormatter(Locale.getDefault());
    }

    /**
     * Builds the formatter from the builder setting the locale.
     * <p>
     * Once the builder is in the correct state it must be converted to a
     * {@code MoneyFormatter} to be used. Calling this method does not
     * change the state of this instance, so it can still be used.
     * <p>
     * This method uses the specified locale within the returned formatter.
     * It can be changed by calling {@link MoneyFormatter#withLocale(Locale)}.
     * 
     * @param locale  the initial locale for the formatter, not null
     * @return the formatter built from this builder, never null
     */
    @SuppressWarnings("cast")
    public MoneyFormatter toFormatter(Locale locale) {
        MoneyFormatter.checkNotNull(locale, "Locale must not be null");
        MoneyPrinter[] printers = (MoneyPrinter[]) iPrinters.toArray(new MoneyPrinter[iPrinters.size()]);
        MoneyParser[] parsers = (MoneyParser[]) iParsers.toArray(new MoneyParser[iParsers.size()]);
        return new MoneyFormatter(locale, printers, parsers);
    }

//    /**
//     * Returns a copy of this instance with the specified pattern.
//     * <p>
//     * The specified pattern is used for positive and zero amounts, while for
//     * negative amounts it is prefixed by the negative sign.
//     * <p>
//     * A pattern is a simple way to define the characters which surround the numeric value.
//     * For example, {@code ${amount} ${code}} will print the ISO code after the value,
//     * producing an output like {@code 12.34 GBP}.
//     * Similarly, {@code -${symbol}${amount}} will produce the output {@code -£12.34}.
//     * <p>
//     * The pattern contains the following elements:<br />
//     * <ul>
//     * <li>{@code ${amount}} : the monetary amount itself
//     * <li>{@code ${code}} : the letter-based currency code, such as 'USD'
//     * <li>{@code ${numericCode}} : the numeric currency code, such as '840'
//     * <li>{@code ${symbol}} : the currency symbol, such as '$'
//     * </ul>
//     * 
//     * @param pattern  the pattern
//     * @return the new instance, never null
//     */
//    public MoneyFormatterBuilder withPattern(String pattern) {
//        return withPattern(pattern, "-" + pattern, pattern);
//    }
//
//    /**
//     * Returns a copy of this instance with the specified positive and negative pattern.
//     * <p>
//     * The specified positive pattern is also used for zero amounts.
//     * <p>
//     * A pattern is a simple way to define the characters which surround the numeric value.
//     * For example, {@code ${amount} ${code}} will print the ISO code after the value,
//     * producing an output like {@code 12.34 GBP}.
//     * Similarly, {@code -${symbol}${amount}} will produce the output {@code -£12.34}.
//     * <p>
//     * The pattern contains the following elements:<br />
//     * <ul>
//     * <li>{@code ${amount}} : the monetary amount itself
//     * <li>{@code ${code}} : the letter-based currency code, such as 'USD'
//     * <li>{@code ${numericCode}} : the numeric currency code, such as '840'
//     * <li>{@code ${symbol}} : the currency symbol, such as '$'
//     * </ul>
//     * 
//     * @param positivePattern  the positive pattern
//     * @param negativePattern  the negative pattern
//     * @return the new instance, never null
//     */
//    public MoneyFormatterBuilder withPattern(String positivePattern, String negativePattern) {
//        return withPattern(positivePattern, negativePattern, positivePattern);
//    }
//
//    /**
//     * Returns a copy of this instance with the specified positive, negative and zero pattern.
//     * <p>
//     * The positive pattern is used for positive amounts, the negative pattern for negative amounts
//     * and the zero pattern for zero amounts.
//     * <p>
//     * A pattern is a simple way to define the characters which surround the numeric value.
//     * For example, {@code ${amount} ${code}} will print the ISO code after the value,
//     * producing an output like {@code 12.34 GBP}.
//     * Similarly, {@code -${symbol}${amount}} will produce the output {@code -£12.34}.
//     * <p>
//     * The pattern contains the following elements:<br />
//     * <ul>
//     * <li>{@code ${amount}} : the monetary amount itself
//     * <li>{@code ${code}} : the letter-based currency code, such as 'USD'
//     * <li>{@code ${numericCode}} : the numeric currency code, such as '840'
//     * <li>{@code ${symbol}} : the currency symbol, such as '$'
//     * </ul>
//     * 
//     * @param positivePattern  the positive pattern
//     * @param negativePattern  the negative pattern
//     * @param zeroPattern  the zero pattern
//     * @return the new instance, never null
//     */
//    public MoneyFormatterBuilder withPattern(String positivePattern, String negativePattern, String zeroPattern) {
//        MoneyUtils.checkNotNull(positivePattern, "Positive pattern must not be null");
//        MoneyUtils.checkNotNull(negativePattern, "Negative pattern must not be null");
//        MoneyUtils.checkNotNull(zeroPattern, "Zero pattern must not be null");
//        return new MoneyFormatterBuilder(iLocale, iZeroCharacter, iDecimalPointCharacter, iGroupingCharacter,
//                iGrouping, iGroupingSize, iForceDecimalPoint, positivePattern, negativePattern, zeroPattern);
//    }

    //-----------------------------------------------------------------------
    /**
     * Handles the amount.
     */
    private static class Amount implements MoneyPrinter, MoneyParser, Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** The style to use. */
        private final MoneyAmountStyle iStyle;
        /**
         * Constructor.
         * @param style  the style, not null
         */
        Amount(MoneyAmountStyle style) {
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

    //-----------------------------------------------------------------------
    /**
     * Handles a literal.
     */
    private static class Literal implements MoneyPrinter, MoneyParser, Serializable {
        /** Serialization version. */
        private static final long serialVersionUID = 1L;
        /** Literal. */
        private final String iLiteral;
        /**
         * Constructor.
         * @param literal  the literal text, not null
         */
        Literal(String literal) {
            iLiteral = literal;
        }
        /** {@inheritDoc} */
        public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
            appendable.append(iLiteral);
        }
        /** {@inheritDoc} */
        public void parse(MoneyParseContext context) {
            int endPos = context.getIndex() + iLiteral.length();
            if (endPos <= context.getTextLength() &&
                    context.getTextSubstring(context.getIndex(), endPos).equals(iLiteral)) {
                context.setIndex(endPos);
            } else {
                context.setError();
            }
        }
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "'" + iLiteral + "'";
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Handles the singleton outputs.
     */
    private static enum Singletons implements MoneyPrinter, MoneyParser {
        CODE("${code}") {
            /** {@inheritDoc} */
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append(money.getCurrencyUnit().getCurrencyCode());
            }
            /** {@inheritDoc} */
            public void parse(MoneyParseContext context) {
                int endPos = context.getIndex() + 3;
                if (endPos > context.getTextLength()) {
                    context.setError();
                } else {
                    String code = context.getTextSubstring(context.getIndex(), endPos);
                    try {
                        context.setCurrency(CurrencyUnit.of(code));
                        context.setIndex(endPos);
                    } catch (IllegalCurrencyException ex) {
                        context.setError();
                    }
                }
            }
        },
        NUMERIC_3_CODE("${numeric3Code}") {
            /** {@inheritDoc} */
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append(money.getCurrencyUnit().getNumeric3Code());
            }
            public void parse(MoneyParseContext context) {
                int endPos = context.getIndex() + 3;
                if (endPos > context.getTextLength()) {
                    context.setError();
                }
                String code = context.getTextSubstring(context.getIndex(), endPos);
                try {
                    context.setCurrency(CurrencyUnit.ofNumericCode(code));
                    context.setIndex(endPos);
                } catch (IllegalCurrencyException ex) {
                    context.setError();
                }
            }
        },
        NUMERIC_CODE("${numericCode}") {
            /** {@inheritDoc} */
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append(Integer.toString(money.getCurrencyUnit().getNumericCode()));
            }
            /** {@inheritDoc} */
            public void parse(MoneyParseContext context) {
                int count = 0;
                for (; count < 3 && context.getIndex() + count < context.getTextLength(); count++) {
                    char ch = context.getText().charAt(context.getIndex() + count);
                    if (ch < '0' || ch > '9') {
                        break;
                    }
                }
                int endPos = context.getIndex() + count;
                String code = context.getTextSubstring(context.getIndex(), endPos);
                try {
                    context.setCurrency(CurrencyUnit.ofNumericCode(code));
                    context.setIndex(endPos);
                } catch (IllegalCurrencyException ex) {
                    context.setError();
                }
            }
        },
        LOCALIZED_SYMBOL("${symbolLocalized}") {
            /** {@inheritDoc} */
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append(money.getCurrencyUnit().getSymbol(context.getLocale()));
            }
            /** {@inheritDoc} */
            public void parse(MoneyParseContext context) {
                throw new UnsupportedOperationException("Unable to parse symbol");
            }
        };
        private final String iToString;
        /** {@inheritDoc} */
        private Singletons(String toString) {
            iToString = toString;
        }
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return iToString;
        }
    }

}
