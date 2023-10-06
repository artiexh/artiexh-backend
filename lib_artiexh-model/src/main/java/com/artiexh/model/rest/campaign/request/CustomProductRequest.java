package com.artiexh.model.rest.campaign.request;

import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProductAttach;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomProductRequest {
	@NotNull
	private Long inventoryItemId;

	private String name;

	private Integer quantity;

	private Money price;

	private Integer limitPerOrder;

	private Long productCategoryId;

	private String description;

	@Valid
	private Set<ProductAttach> attaches;

	private Set<String> tags;
}
