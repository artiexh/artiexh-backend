package com.artiexh.model.rest.providedproduct;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.Size;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProvidedProductType;
import com.artiexh.model.domain.VariantCombination;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.Valid;
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
public class ProvidedProductBaseDetail {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String businessCode;

	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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