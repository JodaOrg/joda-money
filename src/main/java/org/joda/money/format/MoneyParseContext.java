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

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

/**
 * Context used when parsing money.
 * <p>
 * This class is mutable and intended for use by a single thread.
 * A new instance is created for each parse.
 */
public final class MoneyParseContext {

    /**
     * The locale to parse using.
     */
    private Locale locale;
    /**
     * The text to parse.
     */
    private CharSequence text;
    /**
     * The text index.
     */
    private int textIndex;
    /**
     * The text error index.
     */
    private int textErrorIndex = -1;
    /**
     * The parsed currency.
     */
    private CurrencyUnit currency;
    /**
     * The parsed amount.
     */
    private BigDecimal amount;

    /**
     * Constructor.
     * 
     * @param locale  the locale, not null
     * @param text  the text to parse, not null
     * @param index  the current text index
     */
    MoneyParseContext(Locale locale, CharSequence text, int index) {
        this.locale = locale;
        this.text = text;
        this.textIndex = index;
    }

    /**
     * Constructor.
     * 
     * @param locale  the locale, not null
     * @param text  the text to parse, not null
     * @param index  the current text index
     * @param errorIndex  the error index
     * @param currency  the currency
     * @param amount  the parsed amount
     */
    MoneyParseContext(Locale locale, CharSequence text, int index, int errorIndex, CurrencyUnit currency, BigDecimal amount) {
        this.locale = locale;
        this.text = text;
        this.textIndex = index;
        this.textErrorIndex = errorIndex;
        this.currency = currency;
        this.amount = amount;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale.
     * 
     * @return the locale, not null
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the locale.
     * 
     * @param locale  the locale, not null
     */
    public void setLocale(Locale locale) {
        MoneyFormatter.checkNotNull(locale, "Locale must not be null");
        this.locale = locale;
    }

    /**
     * Gets the text being parsed.
     * 
     * @return the text being parsed, never null
     */
    public CharSequence getText() {
        return text;
    }

    /**
     * Sets the text.
     * 
     * @param text  the text being parsed, not null
     */
    public void setText(CharSequence text) {
        MoneyFormatter.checkNotNull(text, "Text must not be null");
        this.text = text;
    }

    /**
     * Gets the length of the text being parsed.
     * 
     * @return the length of the text being parsed
     */
    public int getTextLength() {
        return text.length();
    }

    /**
     * Gets a substring of the text being parsed.
     * 
     * @param start  the start index
     * @param end  the end index
     * @return the substring, not null
     */
    public String getTextSubstring(int start, int end) {
        return text.subSequence(start, end).toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current parse position index.
     * 
     * @return the current parse position index
     */
    public int getIndex() {
        return textIndex;
    }

    /**
     * Sets the current parse position index.
     * 
     * @param index  the current parse position index
     */
    public void setIndex(int index) {
        this.textIndex = index;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the error index.
     * 
     * @return the error index, negative if no error
     */
    public int getErrorIndex() {
        return textErrorIndex;
    }

    /**
     * Sets the error index.
     * 
     * @param index  the error index
     */
    public void setErrorIndex(int index) {
        this.textErrorIndex = index;
    }

    /**
     * Sets the error index from the current index.
     */
    public void setError() {
        this.textErrorIndex = textIndex;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the parsed currency.
     * 
     * @return the parsed currency, null if not parsed yet
     */
    public CurrencyUnit getCurrency() {
        return currency;
    }

    /**
     * Sets the parsed currency.
     * 
     * @param currency  the parsed currency, may be null
     */
    public void setCurrency(CurrencyUnit currency) {
        this.currency = currency;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the parsed amount.
     * 
     * @return the parsed amount, null if not parsed yet
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the parsed currency.
     * 
     * @param amount  the parsed amount, may be null
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the parse has found an error.
     * 
     * @return whether a parse error has occurred
     */
    public boolean isError() {
        return textErrorIndex >= 0;
    }

    /**
     * Checks if the text has been fully parsed such that there is no more text to parse.
     * 
     * @return true if fully parsed
     */
    public boolean isFullyParsed() {
        return textIndex == getTextLength();
    }

    /**
     * Checks if the context contains a currency and amount suitable for creating
     * a monetary value.
     * 
     * @return true if able to create a monetary value
     */
    public boolean isComplete() {
        return currency != null && amount != null;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates a child context.
     * 
     * @return the child context, never null
     */
    MoneyParseContext createChild() {
        return new MoneyParseContext(locale, text, textIndex, textErrorIndex, currency, amount);
    }

    /**
     * Merges the child context back into this instance.
     * 
     * @param child  the child context, not null
     */
    void mergeChild(MoneyParseContext child) {
        setLocale(child.getLocale());
        setText(child.getText());
        setIndex(child.getIndex());
        setErrorIndex(child.getErrorIndex());
        setCurrency(child.getCurrency());
        setAmount(child.getAmount());
    }

    //-----------------------------------------------------------------------
    /**
     * Converts the indexes to a parse position.
     * 
     * @return the parse position, never null
     */
    public ParsePosition toParsePosition() {
        ParsePosition pp = new ParsePosition(textIndex);
        pp.setErrorIndex(textErrorIndex);
        return pp;
    }

    /**
     * Converts the context to a {@code BigMoney}.
     * 
     * @return the monetary value, never null
     * @throws MoneyFormatException if either the currency or amount is missing
     */
    public BigMoney toBigMoney() {
        if (currency == null) {
            throw new MoneyFormatException("Cannot convert to BigMoney as no currency found");
        }
        if (amount == null) {
            throw new MoneyFormatException("Cannot convert to BigMoney as no amount found");
        }
        return BigMoney.of(currency, amount);
    }

}
