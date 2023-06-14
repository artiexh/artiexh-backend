package com.artiexh.model.rest.product;

import com.artiexh.model.domain.ArtistInfo;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.domain.MerchType;
import com.artiexh.model.validation.CurrencyType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class ProductInfo {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String thumbnailUrl;

	@NotNull
	private MerchStatus status;

	@CurrencyType
	@NotEmpty
	private String currency;

	@NotEmpty
	private String name;

	@Min(value = 0)
	@Max(value = 5)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private float averageRate;

	@NotNull
	@Min(value = 0)
	private BigDecimal price;

	@NotNull
	private MerchType type;

	@Min(value = 0)
	@NotNull
	private Long remainingQuantity;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private ArtistInfo ownerInfo;

	@Builder.Default
	private boolean isPreorder = false;
}
