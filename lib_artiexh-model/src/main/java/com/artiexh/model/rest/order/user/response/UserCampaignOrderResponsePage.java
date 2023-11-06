package com.artiexh.model.rest.order.user.response;

import com.artiexh.model.domain.OrderStatus;
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
public class UserCampaignOrderResponsePage {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private CampaignResponse campaign;

	private String note;

	private OrderStatus status;

	private Instant modifiedDate;

	private Instant createdDate;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long orderId;
}
