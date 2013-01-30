
Joda-Money
------------

Joda-Money provides a library of classes to store amounts of money.

Joda-Money does not provide, nor is it intended to provide, monetary algorithms beyond the most basic and obvious.
This is because the requirements for these algorithms vary widely between domains.
This library is intended to act as the base layer, providing classes that should be in the JDK.

As a flavour of Joda-Money, here's some example code:

```java
  // create a monetary value
  Money money = Money.parse("USD 23.87");
  
  // add another amount with safe double conversion
  CurrencyUnit usd = CurrencyUnit.of("USD");
  money = money.plus(Money.of(usd, 12.43d));
  
  // subtracts an amount in dollars
  money = money.minusMajor(2);
  
  // multiplies by 3.5 with rounding
  money = money.multipliedBy(3.5d, RoundingMode.DOWN);
  
  // compare two amounts
  boolean bigAmount = money.isGreaterThan(dailyWage);
  
  // convert to GBP using a supplied rate
  BigDecimal conversionRate = ...;  // obtained from code outside Joda-Money
  Money moneyGBP = money.convertTo(CurrencyUnit.GBP, conversionRate);
  
  // use a BigMoney for more complex calculations where scale matters
  BigMoney moneyCalc = money.toBigMoney();
```

Users are reminded that this software, like all open source software, is provided
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND.

Joda-Money is licensed under the business-friendly [Apache 2.0 licence](https://github.com/JodaOrg/joda-money/blob/master/LICENSE.txt).


### Documentation
Various documentation is available:

* The helpful [user guide](http://joda-money.sourceforge.net/userguide.html)
* The javadoc for the [current release](http://joda-money.sourceforge.net/apidocs/index.html)
* The change notes for the [releases](http://joda-money.sourceforge.net/changes-report.html)


### Releases
[Release 0.7](http://sourceforge.net/projects/joda-money/files/joda-money/0.7/) is the current latest release.
The code is fully tested, but there may yet be bugs and the API may yet change.
There should be no great reason why it cannot be used in production if you can cope with future API change.
It depends on JDK 1.5 or later.

Joda-Money does have a *compile-time* dependency on Joda-Convert, but this is not required at runtime
thanks to the magic of annotations.

Available in the [Maven Central repository](http://search.maven.org/#artifactdetails|org.joda|joda-money|0.7|jar)


### Support
Please use GitHub issues and Pull Requests for support.


### History
Issue tracking and active development is at GitHub.
Historically, the project was at [Sourceforge](https://sourceforge.net/projects/joda-money/).
The [home page](http://joda-money.sourceforge.net/) is still at Sourceforge.
