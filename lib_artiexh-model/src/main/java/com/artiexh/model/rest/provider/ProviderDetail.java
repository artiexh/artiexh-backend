package com.artiexh.model.rest.provider;

import com.artiexh.model.rest.providedproduct.ProvidedModelInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ProviderDetail extends ProviderInfo {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProvidedModelInfo> providedProducts;
}
