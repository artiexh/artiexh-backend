package com.artiexh.model.rest.order.admin.response;

import com.artiexh.model.domain.CampaignOrderStatus;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.User;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCampaignOrderResponsePage {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private User user;

	private String note;

	private PaymentMethod paymentMethod;

	private CampaignOrderStatus status;

	private Instant modifiedDate;

	private Instant createdDate;

	private BigDecimal shippingFee;
}
