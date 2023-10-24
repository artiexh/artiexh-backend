package com.artiexh.model.domain;

import lombok.Getter;

import java.util.Set;

@Getter
public enum CampaignStatus {
	CANCELED(-1),
	DRAFT(0),
	WAITING(1),
	APPROVED(2),
	REQUEST_CHANGE(3),
	REJECTED(4),
	MANUFACTURING(5),
	DONE(6);

	public static final Set<CampaignStatus> ALLOWED_ADMIN_VIEW_STATUS = Set.of(
		CampaignStatus.WAITING,
		CampaignStatus.APPROVED,
		CampaignStatus.REQUEST_CHANGE,
		CampaignStatus.REJECTED,
		CampaignStatus.MANUFACTURING,
		CampaignStatus.DONE
	);

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
