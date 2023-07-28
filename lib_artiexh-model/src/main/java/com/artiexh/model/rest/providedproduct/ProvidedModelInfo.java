package com.artiexh.model.rest.providedproduct;

import com.artiexh.model.domain.Money;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ProvidedModelInfo {
	@JsonSerialize(using = ToStringSerializer.class)
	private long baseModelId;
	private String businessCode;

	private Money price;

	private String description;
}
