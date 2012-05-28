package org.joda.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an exchange rate that allows {@link BigMoneyProvider} instances to be converted between amounts in
 * different {@link CurrencyUnit}s. Exchange rate is an object that holds data about the conversion rate, two
 * {@link CurrencyUnit}s, and optionally scale and rounding mode. It represents the following relationship:
 * 
 * <p>
 * 1 major unit of <strong><code>target currency</code></strong> = <strong><code>rate</code></strong> major units of
 * <strong><code>source currency</code></strong>.
 * </p>
 * 
 * 
 * The optional scale and rounding mode settings are used when rate has to be changed in response to an invoked method,
 * i.e. {@link #invert()}. When those settings are not provided when instances of this class are created defaults are
 * taken from {@link #DEFAULT_SCALE} and {@link ExchangeRate#DEFAULT_ROUNDING_MODE}. <br/>
 * Those settings are not used on creation - this means that if one creates an instance of ExchangeRate with scale 2 and
 * rate with more decimal places the given rate will be used initially, the scale would only be used only when certain
 * methods are invoked (i.e. {@link #invert()}. <br/>
 * An example:
 * 
 * <pre>
 * ExchangeRate o1 = ExchangeRate
 * 		.of(new BigDecimal(&quot;1.2345&quot;), CurrencyUnit.USD, CurrencyUnit.EUR, 2, RoundingMode.HALF_UP);
 * ExchangeRate o2 = o1.invert();
 * </pre>
 * 
 * Instance {@code o1} uses rate 1.2345 for all calculations. Instance {@code o2} has the rate rounded and its scale is
 * set to 2, so the resulting rate is 0.81.
 * 
 * <p>
 * Instances of this class are immutable.
 * </p>
 * 
 * <p>
 * Two instances of this class are considered equal when both target and source currencies are the same and the rates
 * compare equal according to {@link BigDecimal#compareTo(BigDecimal)}. Current scale and rounding mode are not
 * considered while the comparison is performed.
 * </p>
 * 
 * // TODO should negative exchange rates be allowed? Basically it's reasonable that only positive values should be
 * // allowed; if one needs negative amounts one should provide a negative amount to convert.
 * 
 * @author Tom Pasierb
 */
public class ExchangeRate implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_SCALE = 32;
	public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

	/**
	 * Conversion rate between source and target currency.
	 */
	private BigDecimal rate;

	/**
	 * The source currency.
	 */
	private CurrencyUnit source;

	/**
	 * The target currency.
	 */
	private CurrencyUnit target;

	/**
	 * Scale used when internal calculations on the rate are required which is mainly inversion.
	 */
	private int scale;

	/**
	 * Rounding mode to be used when rounding is required.
	 */
	private RoundingMode roundingMode;

	/**
	 * Creates an identity rate for the given {@link CurrencyUnit}. The rate will be equal to 1.00 and both source and
	 * target currency units will be the same as the given one.
	 * 
	 * @param currency
	 *            to be set for source and target currency units.
	 * @throws NullPointerException
	 *             if the given parameter is <code>null</code>
	 * @return identity exchange rate having rate 1 and the same source and target currencies
	 */
	public static ExchangeRate identity(CurrencyUnit currency) {
		return new ExchangeRate(BigDecimal.ONE, currency, currency, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * Creates an Exchange rate with the given parameters and default scale ({@link #DEFAULT_SCALE}) and rounding mode (
	 * {@link #DEFAULT_ROUNDING_MODE}).
	 * 
	 * @param rate
	 *            the conversion rate
	 * @param source
	 *            source {@link CurrencyUnit}
	 * @param target
	 *            target {@link CurrencyUnit}
	 * @throws NullPointerException
	 *             if any of the given parameters is <code>null</code>
	 * @return an instance of exchange rate parameterized as required with default scale and rounding mode
	 */
	public static ExchangeRate of(BigDecimal rate, CurrencyUnit source, CurrencyUnit target) {
		return new ExchangeRate(rate, source, target, DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * Creates an Exchange rate with the given parameters.
	 * 
	 * @param rate
	 *            the conversion rate
	 * @param source
	 *            source {@link CurrencyUnit}
	 * @param target
	 *            target {@link CurrencyUnit}
	 * @param scale
	 *            scale that will be used when rate modifications are needed and the resulting rate cannot be
	 *            represented using a reasonable amount of decimal places
	 * @param roundingMode
	 *            {@link RoundingMode} that will be applied to rate when modification to its value are necessary in
	 *            response to certain methods, i.e. {@link #invert()}
	 * @throws NullPointerException
	 *             if any of the given parameters is {@code null}
	 * @return an instance of exchange rate parameterized as required
	 */
	public static ExchangeRate of(BigDecimal rate, CurrencyUnit source, CurrencyUnit target, int scale,
			RoundingMode roundingMode) {
		return new ExchangeRate(rate, source, target, scale, roundingMode);
	}

	/**
	 * Constructs an instance of ExchangeRate with the given parameters. Initially the given precision and rounding mode
	 * parameters are not used but stored for later usage. The parameters will be used when internal calculation have to
	 * be performed that would result in objects needing rounding.
	 * 
	 * @param rate
	 *            the conversion rate
	 * @param source
	 *            source {@link CurrencyUnit}
	 * @param target
	 *            target {@link CurrencyUnit}
	 * @param scale
	 *            precision (scale) that will be used when rate modifications are needed and the resulting rate cannot
	 *            be represented using a reasonable amount of decimal places
	 * @param roundingMode
	 *            {@link RoundingMode} that will be applied to rate when modification to its value are necessary in
	 *            response to certain methods, i.e. {@link #invert()}
	 * @throws NullPointerException
	 *             if any of the given parameters is <code>null</code>
	 * @return
	 */
	ExchangeRate(BigDecimal rate, CurrencyUnit source, CurrencyUnit target, int scale, RoundingMode roundingMode) {
		Utils.notNull(rate, "Rate must not be null");
		Utils.notNull(source, "Source currency must not be null");
		Utils.notNull(target, "Target currency must not be null");
		Utils.isTrue(scale >= 0, "Scale must not be negative");
		Utils.notNull(roundingMode, "Rate must not be null");
		if (source == target) {
			Utils.isTrue(rate.compareTo(BigDecimal.ONE) == 0,
					"Rate must be 1 if source and target currencies are the same, got rate=%f, source=%s, target=%s",
					rate, source, target);
		}

		this.rate = rate;
		this.source = source;
		this.target = target;
		this.scale = scale;
		this.roundingMode = roundingMode;
	}

	/**
	 * Creates an ExchangeRate like this one with the given rate.
	 * 
	 * @param rate
	 *            the rate that the new ExchangeRate should have
	 * @return a copy of this with the given rate and all other settings as this one
	 */
	public ExchangeRate withRate(BigDecimal rate) {
		return new ExchangeRate(rate, source, target, scale, roundingMode);
	}

	/**
	 * Returns the conversion rate.
	 * 
	 * @return the conversion rate between this exchange rate's source and target currencies
	 */
	public BigDecimal getRate() {
		return rate;
	}

	/**
	 * Returns the source currency.
	 * 
	 * @return the source currency
	 */
	public CurrencyUnit getSource() {
		return source;
	}

	/**
	 * Returns the target currency.
	 * 
	 * @return the target currency
	 */
	public CurrencyUnit getTarget() {
		return target;
	}

	/**
	 * Inverts this exchange rate using this rate's precision and rounding mode. If precision was not specified when
	 * this object was built than a {@link ExchangeRate#DEFAULT_SCALE default scale} and
	 * {@link ExchangeRate#DEFAULT_ROUNDING_MODE default rounding mode} are used.
	 * 
	 * @return ExchangeRate with rate equal to 1 divided by this object's rate and currency units switched
	 */
	public ExchangeRate invert() {
		return of(BigDecimal.ONE.divide(this.rate, scale, roundingMode), target, source);
	}

	/**
	 * Converts the given {@link Money value in currency} to value in the other currency. The conversion is possible if
	 * either source or target currency of this exchange rate matches the currency of the given {@link Money}. This
	 * object's rate is used directly if the given Money's currency is equal to this exchange rate's target currency.
	 * Otherwise this exchange rate is inverted before the conversion is made.
	 * 
	 * The following formula is used for calculation:<br>
	 * 
	 * <p>
	 * 1 major unit of <strong><code>target currency</code></strong> = <strong><code>rate</code></strong> major units of
	 * <strong><code>source currency</code></strong>.
	 * </p>
	 * 
	 * For the following example exchange rate: <code>1 USD = 2.3428 PLN</code><br/>
	 * passing {@link Money 100 USD} as an argument the method will return the equivalent in PLN. On the other hand when
	 * the method receives {@link Money 20 PLN} it will return an equivalent in USD.
	 * 
	 * @param toExchange
	 *            the value in currency to be exchanged
	 * @return the equivalent in other currency
	 * @throws NullPointerException
	 *             if the given parameter is <code>null</code>
	 * @throws NotExchangeableException
	 *             if this exchange rate cannot be used for conversion of the given {@link Money}
	 */
	public BigMoney exchange(BigMoney toExchange) {
		Utils.notNull(toExchange, "Money to exchange cannot be null");

		BigDecimal rate = null;
		CurrencyUnit resultingCurrency = null;

		if (toExchange.getCurrencyUnit().equals(this.target)) {
			resultingCurrency = this.source;
			rate = this.rate;
		} else if (toExchange.getCurrencyUnit().equals(this.source)) {
			resultingCurrency = this.target;
			rate = invert().getRate();
		} else {
			throw new NotExchangeableException(toExchange, this);
		}

		return BigMoney.of(resultingCurrency, toExchange.getAmount().multiply(rate));
	}

	/**
	 * Uses this ExchangeRate's rounding mode for creating the resulting {@link Money} instance.
	 * 
	 * @param toExchange
	 *            the value in currency to be exchanged
	 * @return the equivalent in other currency
	 * @see ExchangeRate#exchange(BigMoney)
	 */
	public Money exchange(Money toExchange) {
		return Money.of(exchange(BigMoney.of(toExchange)), this.roundingMode);
	}

	/**
	 * Uses the rounding mode for creating the resulting {@link Money} instance.
	 * 
	 * @param toExchange
	 *            the value in currency to be exchanged
	 * @return the equivalent in other currency
	 * @see ExchangeRate#exchange(BigMoney)
	 */
	public Money exchange(Money toExchange, RoundingMode roundingMode) {
		return Money.of(exchange(BigMoney.of(toExchange)), roundingMode);
	}

	/**
	 * Combines this object with the given one. This {@link ExchangeRate} and the given one have to have a common
	 * currency no matter the position "source" or "target".
	 * 
	 * This object's non common currency will become the target currency and the other object's non common currency will
	 * become the source currency of the returned object. The common currency will "disappear".
	 * 
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * this rate:  1 USD = 3.50 PLN
	 * other rate: 1 EUR = 4.00 PLN
	 * 
	 * this.combine(other) results in 1 USD = 0.8750 EUR
	 * other.combine(this) results in 1 EUR = 1.1429 USD (rounded to 4 decimal places *)
	 * </pre>
	 * 
	 * A special case is when exchange rates for the same sets of currencies are combined no matter the position. In
	 * this case they may or may not differ on the rate field. Combining two such exchange rate will result in
	 * "identity" rate for <code>this</code> rate's target currency.
	 * 
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * this rate:  1 EUR = 3.22 PLN
	 * other rate: 1 EUR = 3.19 PLN
	 * 
	 * this.combine(other) results in 1 EUR = 1 EUR.
	 * </pre>
	 * 
	 * The resulting ExchangeRate will have the scale and roundingMode of this instance.
	 * <pre>* rounding for this example only, internally scale may be greater</pre>
	 * 
	 * @param other
	 *            the exchange rate to be combine with this instance
	 * @return the combined exchange rate
	 * @throws NullPointerException
	 *             if the other object is null
	 * @throws NoCommonCurrencyException
	 *             if objects this and other have no common currency which means that it is impossible to create a
	 *             combination of the two exchange rates
	 */
	public ExchangeRate combine(ExchangeRate other) {
		Utils.notNull(other, "Exchange rate to be combined must not be null");

		CurrencyUnit commonCurrency = null;

		Set<CurrencyUnit> currencies = new HashSet<CurrencyUnit>();

		currencies.add(other.source);
		currencies.add(other.target);
		if (!currencies.add(this.target))
			commonCurrency = this.target;
		if (!currencies.add(this.source))
			commonCurrency = this.source;

		if (commonCurrency == null) {
			throw new NoCommonCurrencyException(this, other);
		}

		ExchangeRate a = this;
		ExchangeRate b = other;

		if (!a.source.equals(commonCurrency))
			a = a.invert();
		if (!b.source.equals(commonCurrency))
			b = b.invert();

		BigDecimal newRate = null;

		if (b.getTarget() == a.getTarget()) {
			newRate = BigDecimal.ONE;
		} else {
			newRate = a.getRate().divide(b.getRate(), scale, roundingMode).stripTrailingZeros();
		}

		return of(newRate, b.getTarget(), a.getTarget(), scale, roundingMode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExchangeRate other = (ExchangeRate) obj;
		if (rate == null) {
			if (other.rate != null)
				return false;
		} else if (rate.compareTo(other.rate) != 0)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExchangeRate[1 " + target + " = " + rate + " " + source + "]";
	}
}
