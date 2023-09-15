package com.artiexh.model.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {
	private Long id;

	private Long productBaseId;

	private String businessCode;

	private BigDecimal priceAmount;

	private String description;

	private Long maxLimit;

	private String providedProductFileUrl;

	private ProductBase productBase;

	private Set<VariantCombination> variantCombinations;
}