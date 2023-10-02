package com.artiexh.model.rest.campaign.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignRequest {

	@NotNull
	private String providerId;

	@Valid
	@NotEmpty
	private Set<CreateCustomProductRequest> customProducts;

}
