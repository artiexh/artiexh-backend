package com.artiexh.model.rest.campaign.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignRequest {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotNull
	private String providerId;

	@Valid
	private Set<UpdateCustomProductRequest> customProducts;

}
