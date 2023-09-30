package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "custom_product")
public class CustomProductEntity {
	@Id
	@Tsid
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItemEntity inventoryItem;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "campaign_id", nullable = false)
	private CampaignEntity campaign;

	@Size(max = 255)
	@NotNull
	@Column(name = "name", nullable = false)
	private String name;

	@NotNull
	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Size(max = 3)
	@NotNull
	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;

	@NotNull
	@Column(name = "price_amount", nullable = false, precision = 38, scale = 2)
	private BigDecimal priceAmount;

	@Column(name = "limit_per_order")
	private Integer limitPerOrder;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private ProductCategoryEntity category;

	@Size(max = 1000)
	@Column(name = "description", length = 1000)
	private String description;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "custom_product_id")
	private Set<ProductAttachEntity> attaches;

	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "custom_product_id")
	private Set<CustomProductTag> customProductTags = new LinkedHashSet<>();

}
