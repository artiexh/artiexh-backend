package com.artiexh.model.domain;

public enum CampaignStatus {
	DRAFT(-1),
	WAITING(0),
	APPROVED(1),
	REQUEST_CHANGE(2),
	REJECTED(3);

	private final int value;

	CampaignStatus(int value) {
		this.value = value;
	}

	public static CampaignStatus fromValue(int value) {
		for (CampaignStatus status : CampaignStatus.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}
		throw new IllegalArgumentException("Unknown CampaignStatus value: " + value);
	}

	public int getValue() {
		return value;
	}

	public byte getByteValue() {
		return (byte) value;
	}
}
