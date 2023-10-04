package com.artiexh.model.domain;

import lombok.Getter;

@Getter
public enum CampaignHistoryAction {
	CREATE(1),
	SUMMIT(2),
	REQUEST_CHANGE(3),
	APPROVE(4),
	REJECT(5),
	CANCEL(6);

	private final int value;

	CampaignHistoryAction(int value) {
		this.value = value;
	}

	public static CampaignHistoryAction fromValue(int value) {
		return switch (value) {
			case 1 -> CREATE;
			case 2 -> SUMMIT;
			case 3 -> REQUEST_CHANGE;
			case 4 -> APPROVE;
			case 5 -> REJECT;
			default -> throw new IllegalArgumentException("Unknown CampaignHistoryAction value: " + value);
		};
	}

	public byte getByteValue() {
		return (byte) value;
	}
}
