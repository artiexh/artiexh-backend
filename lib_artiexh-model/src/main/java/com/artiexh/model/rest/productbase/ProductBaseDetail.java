package com.artiexh.model.rest.productbase;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderDetail;
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
public class ProductBaseDetail extends ProductBaseInfo {

	private String description;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProviderDetail> providers;
}
