package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Merch {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private ArtistInfo ownerInfo;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private long ownerId;
	private Artist owner;
	private MerchStatus status;
	private String currency;
	private String name;
	private BigDecimal price;
	private String description;
	private MerchType type;
	private Long remainingQuantity;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<String> categories;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private Map<Long, String> categoryInfo;
	private Set<String> tags;
	private Set<MerchAttach> attaches;

}
