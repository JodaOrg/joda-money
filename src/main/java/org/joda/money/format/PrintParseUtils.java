package org.joda.money.format;

import java.io.IOException;
import java.util.Arrays;

import org.joda.money.CurrencyUnit;

class PrintParseUtils {

	public enum CurrencyUnitFormat {
		CODE, NUMERIC_3_CODE, NUMERIC_CODE
	}

	public static void printNumber(PrintContext context, NumberStyle numberStyle, Appendable appendable, String subject)
			throws IOException {
		char zeroChar = numberStyle.getZeroCharacter();
		if (zeroChar != '0') {
			int diff = zeroChar - '0';
			StringBuilder zeroConvert = new StringBuilder(subject);
			for (int i = 0; i < subject.length(); i++) {
				char ch = subject.charAt(i);
				if (ch >= '0' && ch <= '9') {
					zeroConvert.setCharAt(i, (char) (ch + diff));
				}
			}
			subject = zeroConvert.toString();
		}
		int decPoint = subject.indexOf('.');
		if (numberStyle.isGrouping()) {
			int groupingSize = numberStyle.getGroupingSize();
			char groupingChar = numberStyle.getGroupingCharacter();
			int pre = (decPoint < 0 ? subject.length() : decPoint);
			int post = (decPoint < 0 ? 0 : subject.length() - decPoint - 1);
			for (int i = 0; pre > 0; i++, pre--) {
				appendable.append(subject.charAt(i));
				if (pre > 3 && pre % groupingSize == 1) {
					appendable.append(groupingChar);
				}
			}
			if (decPoint >= 0 || numberStyle.isForcedDecimalPoint()) {
				appendable.append(numberStyle.getDecimalPointCharacter());
			}
			decPoint++;
			for (int i = 0; i < post; i++) {
				appendable.append(subject.charAt(i + decPoint));
				if (i % groupingSize == 2) {
					appendable.append(groupingChar);
				}
			}
		} else {
			if (decPoint < 0) {
				appendable.append(subject);
				if (numberStyle.isForcedDecimalPoint()) {
					appendable.append(numberStyle.getDecimalPointCharacter());
				}
			} else {
				appendable.append(subject.subSequence(0, decPoint)).append(numberStyle.getDecimalPointCharacter())
						.append(subject.substring(decPoint + 1));
			}
		}
	}

	public static void printCurrency(PrintContext context, Appendable appendable, CurrencyUnit currency,
			CurrencyUnitFormat format) throws IOException {
		assert context != null;
		assert appendable != null;
		assert currency != null;
		assert format != null;

		String stringForm = "";
		switch (format) {
		case CODE:
			stringForm = currency.getCode();
			break;
		case NUMERIC_3_CODE:
			stringForm = currency.getNumeric3Code();
			break;
		case NUMERIC_CODE:
			stringForm = Integer.toString(currency.getNumericCode());
			break;
		}
		appendable.append(stringForm);
	}

	public static void parseCurrency(ParseContext context, ParseCallback parseCallback) {
		int endPos = context.getIndex() + 3;
		if (endPos > context.getTextLength()) {
			context.setError();
		} else {
			parseCallback.parsed(context.getTextSubstring(context.getIndex(), endPos), endPos);
		}
	}

	public static void parseNumber(ParseContext context, NumberStyle numberStyle, ParseCallback parseCallback) {
		final int len = context.getTextLength();
		char[] buf = new char[len - context.getIndex()];
		int bufPos = 0;
		boolean dpSeen = false;
		boolean lastWasGroup = false;
		int pos = context.getIndex();
		if (pos < len) {
			char ch = context.getText().charAt(pos++);
			if (ch == numberStyle.getNegativeSignCharacter()) {
				buf[bufPos++] = '-';
			} else if (ch == numberStyle.getPositiveSignCharacter()) {
				buf[bufPos++] = '+';
			} else if (ch >= numberStyle.getZeroCharacter() && ch < numberStyle.getZeroCharacter() + 10) {
				buf[bufPos++] = (char) ('0' + ch - numberStyle.getZeroCharacter());
			} else if (ch == numberStyle.getDecimalPointCharacter()) {
				buf[bufPos++] = '.';
				dpSeen = true;
			} else {
				context.setError();
				return;
			}
		}
		for (; pos < len; pos++) {
			char ch = context.getText().charAt(pos);
			if (ch >= numberStyle.getZeroCharacter() && ch < numberStyle.getZeroCharacter() + 10) {
				buf[bufPos++] = (char) ('0' + ch - numberStyle.getZeroCharacter());
				lastWasGroup = false;
			} else if (ch == numberStyle.getDecimalPointCharacter() && dpSeen == false) {
				buf[bufPos++] = '.';
				dpSeen = true;
				lastWasGroup = false;
			} else if (ch == numberStyle.getGroupingCharacter() && lastWasGroup == false) {
				lastWasGroup = true;
			} else {
				break;
			}
		}
		if (lastWasGroup) {
			pos--;
		}
		parseCallback.parsed(String.valueOf(Arrays.copyOf(buf, bufPos)), pos);
	}

	public static interface ParseCallback {

		void parsed(String parsedText, int pos);

	}

	static void verifyParseContext(ParseContext context) {
		if (context.isError() || context.isFullyParsed() == false || !context.isComplete()) {
			String text = context.getText().toString();
			String str = (text.length() > 64 ? text.subSequence(0, 64).toString() + "..." : text.toString());
			if (context.isError()) {
				throw new ParseException("Text could not be parsed at index " + context.getErrorIndex() + ": "
						+ str);
			} else if (context.isFullyParsed() == false) {
				throw new ParseException("Unparsed text found at index " + context.getIndex() + ": " + str);
			} else {
				throw new ComponentsMissingException(str);
			}
		}
	}

	public static class ParseException extends RuntimeException {

		private static final long serialVersionUID = 189319023233939L;

		public ParseException(String message) {
			super(message);
		}

		public ParseException(String message, Throwable cause) {
			super(message, cause);
		}

	}
	
	public static class ComponentsMissingException extends ParseException {

		private static final long serialVersionUID = 5353915232529500033L;

		public ComponentsMissingException(String message, Throwable cause) {
			super(message, cause);
		}

		public ComponentsMissingException(String message) {
			super(message);
		}
		
	}
}
