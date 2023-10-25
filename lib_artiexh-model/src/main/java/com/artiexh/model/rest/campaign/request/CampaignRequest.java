package com.artiexh.model.rest.campaign.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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

	private String description;

	private String thumbnailUrl;

	private String content;

	private String providerId;

	@Valid
	private Set<CustomProductRequest> customProducts = Set.of();

}
