package com.artiexh.model.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {
	PAYING(0),
	PREPARING(1),
	SHIPPING(2),
	COMPLETED(3),
	CANCELLED(4),
	REFUNDED(5);

	private final int value;

	OrderStatus(int value) {
		this.value = value;
	}

	public static OrderStatus fromValue(int value) {
		return switch (value) {
			case 0 -> PAYING;
			case 1 -> PREPARING;
			case 2 -> SHIPPING;
			case 3 -> COMPLETED;
			case 4 -> CANCELLED;
			case 5 -> REFUNDED;
			default -> throw new IllegalArgumentException("No such value for order status: " + value);
		};
	}

	public byte getByteValue() {
		return (byte) value;
	}
}
