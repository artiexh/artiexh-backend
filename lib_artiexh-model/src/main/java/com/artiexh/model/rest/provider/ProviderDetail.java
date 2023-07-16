package com.artiexh.model.rest.provider;

import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import com.artiexh.model.rest.providedproduct.ProvidedProductInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class ProviderDetail extends ProviderInfo{
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProvidedProductInfo> providedProducts;
}
