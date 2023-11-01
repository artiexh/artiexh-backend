package com.artiexh.model.rest.customproduct;

import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.domain.OptionValue;
import com.artiexh.model.domain.ProductOption;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CustomProductDesignResponse extends CustomProductResponse {
	private ProductVariant variant;
	private Set<ImageSet> imageSet;
	private String combinationCode;


	@Data
	@Builder
	public static class ProductVariant {
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
		private Set<ProductVariantCombination> variantCombinations;
		private ProductTemplateInCustomProductResponse productTemplate;
	}

	@Data
	@Builder
	public static class ProductVariantCombination {
		private ProductOption option;
		private OptionValue optionValue;
	}

}
