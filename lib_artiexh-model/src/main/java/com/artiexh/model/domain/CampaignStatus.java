package com.artiexh.model.domain;

import lombok.Getter;

@Getter
public enum CampaignStatus {
	CANCELED(-1),
	DRAFT(0),
	WAITING(1),
	APPROVED(2),
	REQUEST_CHANGE(3),
	REJECTED(4);

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

	public byte getByteValue() {
		return (byte) value;
	}
}
