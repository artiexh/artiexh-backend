package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantCombination {
	private Long variantId;
	private Long optionId;
	private Long optionValueId;
	private Integer quantity;
}
