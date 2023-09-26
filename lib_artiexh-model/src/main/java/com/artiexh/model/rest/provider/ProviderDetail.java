package com.artiexh.model.rest.provider;

import com.artiexh.model.rest.category.ProductCategoryResponse;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import com.artiexh.model.rest.productvariant.ProductVariantDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDetail extends ProviderInfo {
	private String description;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProductVariantDetail> providedProducts;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProductBaseInfo> productBases;

	private String website;
}
