package org.joda.money.format;

import java.text.ParsePosition;
import java.util.Locale;

/**
 * Base class for parse contexts. 
 * 
 * @author Tom Pasierb
 */
public abstract class ParseContext {

	/**
	 * The text to parse.
	 */
	protected CharSequence text;
	
	/**
	 * The text index.
	 */
	protected int textIndex;
	
	/**
	 * The text error index.
	 */
	private int textErrorIndex = -1;
	
	/**
	 * The locale to parse using.
	 */
	protected Locale locale;
	
	/**
	 * Initializes a new instance with the given locale, text and start index.
	 * 
	 * @param locale locale to be used when parsing
	 * @param text the text to parse
	 * @param textIndex the index at which parsing should start
	 */
	ParseContext(Locale locale, CharSequence text, int textIndex) {
		this.locale = locale;
		this.text = text;
		this.textIndex = textIndex;
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
	 * Converts the indexes to a parse position.
	 * 
	 * @return the parse position, never null
	 */
	public ParsePosition toParsePosition() {
	    ParsePosition pp = new ParsePosition(textIndex);
	    pp.setErrorIndex(textErrorIndex);
	    return pp;
	}

	protected abstract boolean isComplete();

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

}
