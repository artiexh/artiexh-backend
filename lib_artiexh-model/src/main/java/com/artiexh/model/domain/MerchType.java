package com.artiexh.model.domain;

public enum MerchType {
	NORMAL(1),
	MEMBER_ONLY(2);

	private final int value;

	MerchType(int value) {
		this.value = value;
	}

	public static MerchType fromValue(int value) {
		for (MerchType type : MerchType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("No such value for MerchType: " + value);
	}

	public int getValue() {
		return value;
	}
}
