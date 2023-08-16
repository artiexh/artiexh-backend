package com.artiexh.model.rest.providedproduct;

import com.artiexh.model.domain.Color;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.Size;
import com.artiexh.model.rest.provider.ProviderInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ProvidedModelInfo {
	@JsonSerialize(using = ToStringSerializer.class)
	private long baseModelId;

	private String businessCode;

	@Valid
	private Money price;

	@NotEmpty
	private List<Size> sizes;

	@NotEmpty
	private Color color;

	@NotEmpty
	private String[] allowConfig;

	@NotEmpty
	private String providedModelFileUrl;

	@NotNull
	@Min(1)
	private Long maxLimit;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private ProviderInfo provider;
}
