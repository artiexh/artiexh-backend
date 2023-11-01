package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_in_campaign")
@EntityListeners(AuditingEntityListener.class)
public class ProductInCampaignEntity extends BaseAuditEntity {
	@Id
	@Tsid
	@JoinColumn(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "custom_product_id", nullable = false)
	private CustomProductEntity customProduct;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "campaign_id", nullable = false)
	private CampaignEntity campaign;

	@Column(name = "name")
	private String name;

	@Column(name = "quantity")
	private Integer quantity;

	@Column(name = "price_unit", length = 3)
	private String priceUnit;

	@Column(name = "price_amount", precision = 38, scale = 2)
	private BigDecimal priceAmount;

	@Column(name = "limit_per_order")
	private Integer limitPerOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private ProductCategoryEntity category;

	@Column(name = "description", length = 1000)
	private String description;

	@Builder.Default
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "product_in_campaign_id", updatable = false)
	private Set<ProductInCampaignTagEntity> tags = new LinkedHashSet<>();

}
