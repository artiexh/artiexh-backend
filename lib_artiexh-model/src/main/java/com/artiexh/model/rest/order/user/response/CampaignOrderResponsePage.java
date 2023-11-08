package com.artiexh.model.rest.order.user.response;

import com.artiexh.model.domain.CampaignOrderStatus;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CampaignOrderResponsePage {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private CampaignResponse campaign;

	private String note;

	private CampaignOrderStatus status;

	private Instant modifiedDate;

	private Instant createdDate;
}
