package org.joda.money.format;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.BigMoneyProvider;
import org.joda.money.Money;

/**
 * Formats instances of money to and from a String.
 * <p>
 * Instances of {@code MoneyFormatter} can be created by
 * {@code MoneyFormatterBuilder}.
 * <p>
 * This class is immutable and thread-safe.
 */
public final class MoneyFormatter implements Serializable {

    private static final long serialVersionUID = 2385346258L;

    // ✅ Pull-up variable (Magic Number removed)
    private static final int MAX_PREVIEW_LENGTH = 64;

    private final Locale locale;
    private final MultiPrinterParser printerParser;

    //-----------------------------------------------------------------------
    static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    //-----------------------------------------------------------------------
    MoneyFormatter(Locale locale, MoneyPrinter[] printers, MoneyParser[] parsers) {
        checkNotNull(locale, "Locale must not be null");
        checkNotNull(printers, "Printers must not be null");
        checkNotNull(parsers, "Parsers must not be null");
        if (printers.length != parsers.length) {
            throw new IllegalArgumentException("Printers and parsers must match");
        }
        this.locale = locale;
        this.printerParser = new MultiPrinterParser(printers, parsers);
    }

    private MoneyFormatter(Locale locale, MultiPrinterParser printerParser) {
        checkNotNull(locale, "Locale must not be null");
        checkNotNull(printerParser, "PrinterParser must not be null");
        this.locale = locale;
        this.printerParser = printerParser;
    }

    MultiPrinterParser getPrinterParser() {
        return printerParser;
    }

    public Locale getLocale() {
        return locale;
    }

    public MoneyFormatter withLocale(Locale locale) {
        checkNotNull(locale, "Locale must not be null");
        return new MoneyFormatter(locale, printerParser);
    }

    public boolean isPrinter() {
        return printerParser.isPrinter();
    }

    public boolean isParser() {
        return printerParser.isParser();
    }

    public String print(BigMoneyProvider moneyProvider) {
        var buf = new StringBuilder();
        print(buf, moneyProvider);
        return buf.toString();
    }

    public void print(Appendable appendable, BigMoneyProvider moneyProvider) {
        try {
            printIO(appendable, moneyProvider);
        } catch (IOException ex) {
            throw new MoneyFormatException(ex.getMessage(), ex);
        }
    }

    public void printIO(Appendable appendable, BigMoneyProvider moneyProvider) throws IOException {
        checkNotNull(moneyProvider, "BigMoneyProvider must not be null");
        if (!isPrinter()) {
            throw new UnsupportedOperationException("MoneyFomatter has not been configured to be able to print");
        }

        var money = BigMoney.of(moneyProvider);
        var context = new MoneyPrintContext(locale);
        printerParser.print(context, appendable, money);
    }

    public BigMoney parseBigMoney(CharSequence text) {
        checkNotNull(text, "Text must not be null");
        var result = parse(text, 0);
        if (result.isError() || !result.isFullyParsed() || !result.isComplete()) {


            String previewText = getPreviewText(text);

            if (result.isError()) {
                throw new MoneyFormatException("Text could not be parsed at index " + result.getErrorIndex() + ": " + previewText);
            } else if (!result.isFullyParsed()) {
                throw new MoneyFormatException("Unparsed text found at index " + result.getIndex() + ": " + previewText);
            } else {
                throw new MoneyFormatException("Parsing did not find both currency and amount: " + previewText);
            }
        }
        return result.toBigMoney();
    }


    private String getPreviewText(CharSequence text) {
        return (text.length() > MAX_PREVIEW_LENGTH)
                ? text.subSequence(0, MAX_PREVIEW_LENGTH).toString() + "..."
                : text.toString();
    }

    public Money parseMoney(CharSequence text) {
        return parseBigMoney(text).toMoney();
    }

    public MoneyParseContext parse(CharSequence text, int startIndex) {
        checkNotNull(text, "Text must not be null");
        if (startIndex < 0 || startIndex > text.length()) {
            throw new StringIndexOutOfBoundsException("Invalid start index: " + startIndex);
        }
        if (!isParser()) {
            throw new UnsupportedOperationException("MoneyFomatter has not been configured to be able to parse");
        }
        var context = new MoneyParseContext(locale, text, startIndex);
        printerParser.parse(context);
        return context;
    }

    @Override
    public String toString() {
        return printerParser.toString();
    }
}
