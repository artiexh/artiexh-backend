package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantCombination {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long variantId;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long optionId;
	@JsonSerialize(using = ToStringSerializer.class)
	private Long optionValueId;
	private boolean isOptional;
}
