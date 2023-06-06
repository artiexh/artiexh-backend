package com.artiexh.model.product;

import com.artiexh.model.domain.ArtistInfo;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.domain.MerchType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Map;

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
	private MerchStatus status;
	private String currency;
	private String name;
	private BigDecimal price;
	private MerchType type;
	private Long remainingQuantity;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(readOnly = true)
	private ArtistInfo ownerInfo;

}
