package com.artiexh.model.rest.marketplace.salecampaign.request;

import com.artiexh.model.domain.CampaignType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleCampaignRequest {
	@NotBlank
	private String name;

	private String description;

	@NotNull
	@FutureOrPresent
	private Instant publicDate;

	@NotNull
	@FutureOrPresent
	private Instant from;

	@NotNull
	@Future
	private Instant to;

	private String content;

	private String thumbnailUrl;

	@NotNull
	private CampaignType type;

	@Valid
	@NotBlank
	private Set<ProductInSaleRequest> products = Set.of();
}
