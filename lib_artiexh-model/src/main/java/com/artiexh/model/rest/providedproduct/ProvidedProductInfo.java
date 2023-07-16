package com.artiexh.model.rest.providedproduct;

import com.artiexh.data.jpa.entity.ProvidedProductId;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ProvidedProductInfo {
	private long baseModelId;

	private String businessCode;

	private BigDecimal priceAmount;

	private String priceUnit;

	private String description;
}
