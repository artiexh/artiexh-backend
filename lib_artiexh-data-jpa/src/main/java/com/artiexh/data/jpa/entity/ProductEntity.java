package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductEntity {
	@EmbeddedId
	private ProductEntityId id;

	@MapsId("productCode")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_code", nullable = false)
	private ProductInventoryEntity productInventory;

	@MapsId("campaignSaleId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "campaign_sale_id", nullable = false)
	private CampaignSaleEntity campaignSale;

	@Column(name = "price_amount", nullable = false, precision = 38, scale = 2)
	private BigDecimal priceAmount;

	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "sold_quantity")
	private Integer soldQuantity;

}