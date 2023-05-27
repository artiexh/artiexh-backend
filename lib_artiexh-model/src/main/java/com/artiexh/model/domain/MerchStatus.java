package com.artiexh.model.domain;

public enum MerchStatus {
	DELETED(-1),
	NOT_AVAILABLE(0),
	PRE_ORDER(1),
	AVAILABLE(2);

	private final int value;

	MerchStatus(int value) {
		this.value = value;
	}

	public static MerchStatus fromValue(int value) {
		for (MerchStatus status : MerchStatus.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("No such value for MerchStatus: " + value);
	}

	public int getValue() {
		return value;
	}
}
