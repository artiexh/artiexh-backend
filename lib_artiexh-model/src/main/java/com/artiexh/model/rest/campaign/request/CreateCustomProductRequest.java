package com.artiexh.model.rest.campaign.request;

import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProductAttach;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomProductRequest {
	@NotNull
	private Long inventoryItemId;

	@NotBlank
	private String name;

	@NotNull
	private Integer quantity;

	@Valid
	private Money price;

	private Integer limitPerOrder;

	@NotNull
	private Long productCategoryId;

	private String description;

	@Valid
	private Set<ProductAttach> attaches;

	private Set<String> tags;
}
