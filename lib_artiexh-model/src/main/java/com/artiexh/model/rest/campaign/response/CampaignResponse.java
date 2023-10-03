package com.artiexh.model.rest.campaign.response;

import com.artiexh.model.domain.CampaignStatus;
import com.artiexh.model.rest.product.response.ProductResponse;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private CampaignStatus status;
	private Owner owner;
	private Set<CustomProductResponse> customProducts;


	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Owner {
		@JsonSerialize(using = ToStringSerializer.class)
		private String id;
		private String username;
		private String displayName;
		private String avatarUrl;
		private ProductResponse.Province province;
	}
}
