package com.artiexh.model.rest.basemodel;

import com.artiexh.data.jpa.entity.ProvidedModelEntity;
import com.artiexh.model.rest.providedproduct.ProvidedModelInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseModelDetail extends BaseModelInfo {
	@NotEmpty
	private String description;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProvidedModelInfo> providedModels;
}
