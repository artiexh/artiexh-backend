package com.artiexh.model.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum CampaignOrderStatus {
	PAYING(0),
	PREPARING(1),
	SHIPPING(2),
	COMPLETED(3),
	CANCELED(4),
	REFUNDED(5);

	private final int value;

	CampaignOrderStatus(int value) {
		this.value = value;
	}

	public static Set<CampaignOrderStatus> ALLOWED_CANCEL_STATUS = Set.of(PAYING, PREPARING, SHIPPING);

	public static CampaignOrderStatus fromValue(int value) {
		return switch (value) {
			case 0 -> PAYING;
			case 1 -> PREPARING;
			case 2 -> SHIPPING;
			case 3 -> COMPLETED;
			case 4 -> CANCELED;
			case 5 -> REFUNDED;
			default -> throw new IllegalArgumentException("No such value for order status: " + value);
		};
	}

	public byte getByteValue() {
		return (byte) value;
	}
}
