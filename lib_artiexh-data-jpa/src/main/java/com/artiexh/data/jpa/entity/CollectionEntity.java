package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "collection")
public class CollectionEntity {
	@Id
	@Tsid
	private Long id;
	private String name;
	@Column(name = "image_url")
	private String imageUrl;
	@Column(name = "price_amount")
	private BigDecimal priceAmount;
	@Column(name = "business_code")
	private String businessCode;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinTable(
		name = "collection_provided_product_mapping",
		joinColumns = @JoinColumn(name = "collection_id"),
		inverseJoinColumns = @JoinColumn(name = "provided_product_id"))
	private Set<ProvidedProductBaseEntity> providedProducts;
}
