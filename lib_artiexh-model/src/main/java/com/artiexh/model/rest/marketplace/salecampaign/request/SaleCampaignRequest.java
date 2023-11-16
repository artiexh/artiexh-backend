package com.artiexh.model.rest.marketplace.salecampaign.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
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
	@FutureOrPresent
	private Instant publicDate;
	@FutureOrPresent
	private Instant from;
	@Future
	private Instant to;
	@Valid
	private Set<ProductInSaleRequest> products = Set.of();
}
