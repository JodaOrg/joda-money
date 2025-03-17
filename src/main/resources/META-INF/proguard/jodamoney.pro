# Keep the DefaultCurrencyUnitDataProvider class and its constructor
# as it is accessed via reflection in the static initializer of CurrencyUnit
-keep class org.joda.money.DefaultCurrencyUnitDataProvider {
    <init>();
}
