package com.artiexh.model.rest.providedproduct;

import com.artiexh.model.rest.basemodel.BaseModelInfo;
import com.artiexh.model.rest.provider.ProviderInfo;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ProvidedModelDetail extends ProvidedModelInfo {
	@NotEmpty
	private String description;

	private ProviderInfo provider;

	private BaseModelInfo baseModel;
}
