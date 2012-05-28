package org.joda.money;

public class NoCommonCurrencyException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private final ExchangeRate _this;
	private final ExchangeRate _other;

	public NoCommonCurrencyException(ExchangeRate _this, ExchangeRate _other) {
		super(String.format("Exchange rates have no common currency: this=%s, other=%s", _this, _other));
		this._this = _this;
		this._other = _other;
	}

	public ExchangeRate get_this() {
		return _this;
	}

	public ExchangeRate get_other() {
		return _other;
	}

}
