package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInCampaign {
	private Long id;
	private CustomProduct customProduct;
	private Campaign campaign;
	private String name;
	private Integer quantity;
	private Money price;
	private Integer limitPerOrder;
	private ProductCategory category;
	private String description;
	private Set<String> tags;
	private Instant createdDate;
	private Instant modifiedDate;
}
