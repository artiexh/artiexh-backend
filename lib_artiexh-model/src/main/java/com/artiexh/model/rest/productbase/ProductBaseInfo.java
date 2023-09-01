package com.artiexh.model.rest.productbase;

import com.artiexh.model.domain.Model3DCode;
import com.artiexh.model.domain.Money;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseInfo {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String type;

	@NotBlank
	private String productFileUrl;

	@NotNull
	@Valid
	private Money price;

	private Model3DCode model3DCode;
}
