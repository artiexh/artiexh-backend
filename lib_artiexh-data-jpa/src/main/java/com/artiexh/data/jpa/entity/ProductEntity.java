package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product")
public class ProductEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "owner_id", nullable = false)
	private ArtistEntity owner;

	@Column(name = "price_amount", nullable = false)
	private BigDecimal priceAmount;

	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;

	@Column(name = "quantity", columnDefinition = "INT UNSIGNED not null")
	private Long quantity;

	@Builder.Default
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "product_bundle_mapping",
		joinColumns = @JoinColumn(name = "product_id"),
		inverseJoinColumns = @JoinColumn(name = "bundle_id"))
	private Set<ProductEntity> bundles = new LinkedHashSet<>();

	@OneToOne()
	@JoinColumn(
		name = "campaign_sale_id",
		referencedColumnName = "id",
		updatable = false,
		insertable = false
	)
	private CampaignEntity campaign;

	@Column(name = "campaign_id")
	private Long campaignId;

	@Builder.Default
	@Column(name = "sold_quantity", nullable = false)
	private Long soldQuantity = 0L;

	@OneToOne(orphanRemoval = true,
		fetch = FetchType.LAZY
	)
	@JoinColumn(name = "product_in_campaign_id", updatable = false, insertable = false)
	private ProductInCampaignEntity productInCampaign;

	@Column(name = "product_in_campaign_id")
	private Long productInCampaignId;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "created_by", nullable = false)
	private ArtistEntity shop;

	@Column(name = "status", nullable = false)
	private Byte status;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToOne(optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "category_id", nullable = false)
	private ProductCategoryEntity category;

	@Column(name = "description")
	private String description;

	@Column(name = "type", nullable = false)
	private Byte type;

	@Column(name = "max_items_per_order", columnDefinition = "INT UNSIGNED")
	private Long maxItemsPerOrder;

	@Column(name = "delivery_type", nullable = false)
	private Byte deliveryType;

	@Builder.Default
	@Column(name = "average_rate", nullable = false)
	private Float averageRate = 0f;

	@Column(name = "payment_method")
	@Lob
	private Byte[] paymentMethods;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "product_tag_mapping", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<ProductTagEntity> tags;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "product_id")
	private Set<ProductAttachEntity> attaches;

	@NotNull
	@Column(name = "weight", nullable = false)
	private Float weight;

	@Builder.Default
	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(name = "product_bundle_mapping",
		joinColumns = @JoinColumn(name = "bundle_id"),
		inverseJoinColumns = @JoinColumn(name = "product_id"))
	private Set<ProductEntity> bundleItems = new LinkedHashSet<>();
}
