package com.artiexh.model.domain;

public enum ProductStatus {
	DELETED(-1),
	NOT_AVAILABLE(0),
	PRE_ORDER(1),
	AVAILABLE(2);

	private final int value;

	ProductStatus(int value) {
		this.value = value;
	}

	public static ProductStatus fromValue(int value) {
		for (ProductStatus status : ProductStatus.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("No such value for ProductStatus: " + value);
	}

	public int getValue() {
		return value;
	}

	public byte getByteValue() {
		return (byte) value;
	}
}
