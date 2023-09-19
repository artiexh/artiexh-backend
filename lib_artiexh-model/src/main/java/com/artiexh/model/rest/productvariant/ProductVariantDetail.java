package com.artiexh.model.rest.productvariant;

import com.artiexh.model.domain.VariantCombination;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDetail {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long productBaseId;

	@NotEmpty
	private Set<ProviderConfig> providerConfigs;

	@NotBlank
	private String description;

	@NotNull
	@Min(1)
	private Long maxLimit;

	@NotBlank
	private String providedProductFileUrl;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(allOf = ProductBaseInfo.class)
	private ProductBaseInfo productBase;

	@NotEmpty
	private List<VariantCombination> variantCombinations;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProviderConfig {

		@NotNull
		private String businessCode;

		@NotNull
		private BigDecimal basePriceAmount;
	}
}
