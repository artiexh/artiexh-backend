package com.artiexh.ghtk.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {
	private String hash;
	@JsonProperty("label_id")
	private String labelId;
	@JsonProperty("partner_id")
	private String partnerId;
	@JsonProperty("status_id")
	private Integer statusId;
	@JsonProperty("action_time")
	private String actionTime;
	@JsonProperty("reason_code")
	private String reasonCode;
	private String reason;
	private Float weight;
	private Integer fee;
	@JsonProperty("return_part_package")
	private Integer returnPartPackage;
}
