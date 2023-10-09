package com.artiexh.model.rest.campaign.response;

import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.domain.ProductCategory;
import com.artiexh.model.domain.ProductVariantProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CustomProductResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String name;
	private Integer quantity;
	private Money price;
	private Integer limitPerOrder;
	private ProductCategory category;
	private String description;
	private Set<ProductAttach> attaches;
	private Set<String> tags;
	private InventoryItemResponse inventoryItem;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private ProductVariantProvider providerConfig;
}
