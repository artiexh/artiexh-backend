package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
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

//	@OneToOne()
//	@JoinColumn(
//		name = "campaign_sale_id",
//		referencedColumnName = "id",
//		updatable = false,
//		insertable = false
//	)
//	private CampaignEntity campaign;

	@Column(name = "campaign_sale_id")
	private Long saleCampaignId;

	@Builder.Default
	@Column(name = "sold_quantity", nullable = false)
	private Long soldQuantity = 0L;

}
