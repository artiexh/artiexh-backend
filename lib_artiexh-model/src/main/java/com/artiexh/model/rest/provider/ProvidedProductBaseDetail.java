package com.artiexh.model.rest.provider;

import com.artiexh.data.jpa.entity.Color;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.data.jpa.entity.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvidedProductBaseDetail {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String businessCode;

	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long productBaseId;

	private BigDecimal priceAmount;

	private String priceUnit;

	private String description;

	private Color color;

	private List<Size> sizes;

	private Long maxLimit;

	private String[] allowConfig;

	private String providedProductFileUrl;
}
