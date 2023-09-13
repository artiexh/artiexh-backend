package com.artiexh.model.rest.provider;

import com.artiexh.model.rest.providedproduct.ProvidedProductBaseDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDetail extends ProviderInfo{
	private String description;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProvidedProductBaseDetail> providedProducts;
}
