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

import java.math.BigDecimal;
import java.text.ParsePosition;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

/**
 * Context used when parsing money.
 * <p>
 * MoneyParseContext is mutable and not thread-safe.
 * A new instance is created for each parse.
 */
public final class MoneyParseContext {

    /**
     * The locale to parse using.
     */
    private final Locale iLocale;
    /**
     * The text to parse.
     */
    private final CharSequence iText;
    /**
     * The text index.
     */
    private int iTextIndex;
    /**
     * The text error index.
     */
    private int iTextErrorIndex = -1;
    /**
     * The parsed currency.
     */
    private CurrencyUnit iCurrency;
    /**
     * The parsed amount.
     */
    private BigDecimal iAmount;

    /**
     * Constructor.
     * 
     * @param locale  the locale, not null
     * @param text  the text to parse, not null
     * @param index  the current text index
     */
    MoneyParseContext(Locale locale, CharSequence text, int index) {
        iLocale = locale;
        iText = text;
        iTextIndex = index;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the locale.
     * 
     * @return the locale, never null
     */
    public Locale getLocale() {
        return iLocale;
    }

    /**
     * Gets the text being parsed.
     * 
     * @return the text being parsed, never null
     */
    public CharSequence getText() {
        return iText;
    }

    /**
     * Gets the length of the text being parsed.
     * 
     * @return the length of the text being parsed
     */
    public int getTextLength() {
        return iText.length();
    }

    /**
     * Gets a substring of the text being parsed.
     * 
     * @param start  the start index
     * @param end  the end index
     * @return the substring, not null
     */
    public String getTextSubstring(int start, int end) {
        return iText.subSequence(start, end).toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the current parse position index.
     * 
     * @return the current parse position index
     */
    public int getIndex() {
        return iTextIndex;
    }

    /**
     * Sets the current parse position index.
     * 
     * @param index  the current parse position index
     */
    public void setIndex(int index) {
        iTextIndex = index;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the error index.
     * 
     * @return the error index, negative if no error
     */
    public int getErrorIndex() {
        return iTextIndex;
    }

    /**
     * Sets the error index.
     * 
     * @param index  the error index
     */
    public void setErrorIndex(int index) {
        iTextErrorIndex = index;
    }

    /**
     * Sets the error index from the current index.
     */
    public void setError() {
        iTextErrorIndex = iTextIndex;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the parsed currency.
     * 
     * @return the parsed currency, null if not parsed yet
     */
    public CurrencyUnit getCurrency() {
        return iCurrency;
    }

    /**
     * Sets the parsed currency.
     * 
     * @param curency  the parsed currency, may be null
     */
    public void setCurrency(CurrencyUnit currency) {
        this.iCurrency = currency;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the parsed amount.
     * 
     * @return the parsed amount, null if not parsed yet
     */
    public BigDecimal getAmount() {
        return iAmount;
    }

    /**
     * Sets the parsed currency.
     * 
     * @param amount  the parsed amount, may be null
     */
    public void setAmount(BigDecimal amount) {
        this.iAmount = amount;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the parse has found an error.
     * 
     * @return whether a parse error has occurred
     */
    public boolean isError() {
        return iTextErrorIndex >= 0;
    }

    /**
     * Checks if the text has been fully parsed such that there is no more text to parse.
     * 
     * @return true if fully parsed
     */
    public boolean isFullyParsed() {
        return iTextIndex == getTextLength();
    }

    /**
     * Checks if the context contains a currency and amount suitable for creating
     * a monetary value.
     * 
     * @return true if able to create a monetary value
     */
    public boolean isComplete() {
        return iCurrency != null && iAmount != null;
    }

    //-----------------------------------------------------------------------
    /**
     * Converts the indexes to a parse position.
     * 
     * @return the parse position, never null
     */
    public ParsePosition toParsePosition() {
        ParsePosition pp = new ParsePosition(iTextIndex);
        pp.setErrorIndex(iTextErrorIndex);
        return pp;
    }

    /**
     * Converts the context to a {@code BigMoney}.
     * 
     * @return the monetary value, never null
     * @throws MoneyFormatException if either the currency or amount is missing
     */
    public BigMoney toBigMoney() {
        if (iCurrency == null) {
            throw new MoneyFormatException("Cannot convert to BigMoney as no currency found");
        }
        if (iAmount == null) {
            throw new MoneyFormatException("Cannot convert to BigMoney as no amount found");
        }
        return BigMoney.of(iCurrency, iAmount);
    }

}
