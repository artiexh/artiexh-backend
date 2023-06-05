package com.artiexh.model.product;

import com.artiexh.model.domain.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

public class ProductDetail {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(readOnly = true)
	private ArtistInfo ownerInfo;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private long ownerId;
	private MerchStatus status;
	private String name;
	private Double price;
	private String description;
	private MerchType type;
	private Long remainingQuantity;
	private LocalDateTime publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Set<String> categories;
	private Set<String> tags;
	private Set<MerchAttach> attaches;
}
