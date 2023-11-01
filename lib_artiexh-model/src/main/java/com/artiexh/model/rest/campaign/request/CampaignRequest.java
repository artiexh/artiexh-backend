package com.artiexh.model.rest.campaign.request;

import com.artiexh.model.domain.CampaignType;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CampaignRequest {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotEmpty
	private String name;

	@NotNull
	private CampaignType type;

	private String description;

	private String thumbnailUrl;

	private String content;

	private String providerId;

	@Valid
	private Set<ProductInCampaignRequest> products = Set.of();

}
