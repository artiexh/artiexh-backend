package com.artiexh.model.rest.productvariant;

import com.artiexh.model.domain.VariantCombination;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class ProductVariantDetail {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	@NotNull
	private String businessCode;

	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long productBaseId;

	@NotNull
	private BigDecimal priceAmount;

	@NotBlank
	private String description;

	@NotNull
	@Min(1)
	private Long maxLimit;

	@NotBlank
	private String providedProductFileUrl;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private ProductBaseInfo productBase;

	@NotEmpty
	private List<VariantCombination> variantCombinations;
}
