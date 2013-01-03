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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;

/**
 * Provides the ability to build a formatter for monetary values.
 * <p>
 * This class is mutable and intended for use by a single thread.
 * A new instance should be created for each use.
 * The formatters produced by the builder are immutable and thread-safe.
 */
public final class MoneyFormatterBuilder {

    /**
     * The printers.
     */
    private final List<MoneyPrinter> printers = new ArrayList<MoneyPrinter>();
    /**
     * The parsers.
     */
    private final List<MoneyParser> parsers = new ArrayList<MoneyParser>();

    //-----------------------------------------------------------------------
    /**
     * Constructor, creating a new empty builder.
     */
    public MoneyFormatterBuilder() {
    }

    //-----------------------------------------------------------------------
    /**
     * Appends the amount to the builder using a standard format.
     * <p>
     * The format used is {@link MoneyAmountStyle#ASCII_DECIMAL_POINT_GROUP3_COMMA}.
     * The amount is the value itself, such as '12.34'.
     * 
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendAmount() {
        AmountPrinterParser pp = new AmountPrinterParser(MoneyAmountStyle.ASCII_DECIMAL_POINT_GROUP3_COMMA);
        return appendInternal(pp, pp);
    }

    /**
     * Appends the amount to the builder using a grouped localized format.
     * <p>
     * The format used is {@link MoneyAmountStyle#LOCALIZED_GROUPING}.
     * The amount is the value itself, such as '12.34'.
     * 
     * @return this, for chaining, never null
     */
    public MoneyFormatterBuilder appendAmountLocalized() {
        AmountPrinterParser pp = new AmountPrinterParser(MoneyAmountStyle.LOCALIZED_GROUPING);
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
        AmountPrinterParser pp = new AmountPrinterParser(style);
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
        return appendInternal(SingletonPrinters.LOCALIZED_SYMBOL, null);
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
        LiteralPrinterParser pp = new LiteralPrinterParser(literal.toString());
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
        printers.add(printer);
        parsers.add(parser);
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
        MoneyPrinter[] printersCopy = (MoneyPrinter[]) printers.toArray(new MoneyPrinter[printers.size()]);
        MoneyParser[] parsersCopy = (MoneyParser[]) parsers.toArray(new MoneyParser[parsers.size()]);
        return new MoneyFormatter(locale, printersCopy, parsersCopy);
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
     * Handles the singleton outputs.
     */
    private static enum Singletons implements MoneyPrinter, MoneyParser {
        CODE("${code}") {
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append(money.getCurrencyUnit().getCode());
            }
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
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append(money.getCurrencyUnit().getNumeric3Code());
            }
            public void parse(MoneyParseContext context) {
                int endPos = context.getIndex() + 3;
                if (endPos > context.getTextLength()) {
                    context.setError();
                } else {
                    String code = context.getTextSubstring(context.getIndex(), endPos);
                    try {
                        context.setCurrency(CurrencyUnit.ofNumericCode(code));
                        context.setIndex(endPos);
                    } catch (IllegalCurrencyException ex) {
                        context.setError();
                    }
                }
            }
        },
        NUMERIC_CODE("${numericCode}") {
            public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
                appendable.append(Integer.toString(money.getCurrencyUnit().getNumericCode()));
            }
            public void parse(MoneyParseContext context) {
                int count = 0;
                for ( ; count < 3 && context.getIndex() + count < context.getTextLength(); count++) {
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
        };
        private final String toString;
        private Singletons(String toString) {
            this.toString = toString;
        }
        @Override
        public String toString() {
            return toString;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Handles the singleton outputs.
     */
    private static enum SingletonPrinters implements MoneyPrinter {
        LOCALIZED_SYMBOL;
        public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
            appendable.append(money.getCurrencyUnit().getSymbol(context.getLocale()));
        }
        @Override
        public String toString() {
            return "${symbolLocalized}";
        }
    }

}
