package org.joda.money.format;

public interface NumberStyle {

	Character getZeroCharacter();

	boolean isGrouping();

	Integer getGroupingSize();

	Character getGroupingCharacter();

	boolean isForcedDecimalPoint();

	Character getDecimalPointCharacter();

	Character getNegativeSignCharacter();

	Character getPositiveSignCharacter();

}
