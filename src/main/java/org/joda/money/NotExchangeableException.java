package org.joda.money;


public class NotExchangeableException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private final BigMoneyProvider money;
	
	private final ExchangeRate exchangeRate;

	public NotExchangeableException(BigMoneyProvider money, ExchangeRate exchangeRate) {
		super(String.format("%s is not exchangeable using %s", money != null ? money : "Money <null>",
				exchangeRate != null ? exchangeRate : "ExchangeRate <null>"));
		this.money = money;
		this.exchangeRate = exchangeRate;
	}

	public BigMoneyProvider getMoney() {
		return money;
	}

	public ExchangeRate getExchangeRate() {
		return exchangeRate;
	}

}
