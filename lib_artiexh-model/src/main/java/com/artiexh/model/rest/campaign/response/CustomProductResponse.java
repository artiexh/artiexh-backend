package com.artiexh.model.rest.campaign.response;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.model.domain.ImageSet;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomProductResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String name;
	private String combinationCode;
	private Set<ImageSet> imageSet;
	private String description;
	private ProductTemplateResponse productTemplate;
	private VariantResponse variant;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProductTemplateResponse {
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
		private String name;
		private List<ImageCombination> imageCombinations;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class VariantResponse {
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
		private Set<VariantCombinationResponse> variantCombination;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class VariantCombinationResponse {
		private String optionName;
		private String valueName;
		private String value;
	}

}
