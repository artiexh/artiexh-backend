package com.artiexh.model.rest.provider;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderConfigResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long variantId;
	private BigDecimal basePriceAmount;
	private String manufacturingTime;
	private Integer minQuantity;
}
