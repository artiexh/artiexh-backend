package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provided_product")
@Builder(toBuilder = true)
public class ProvidedProductBaseEntity {
	@Id
	@Tsid
	private Long id;

	@Embedded
	private ProvidedProductBaseId providedProductBaseId;

	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "product_base_id", insertable = false, updatable = false)
	private ProductBaseEntity productBase;

	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "business_code", insertable = false, updatable = false)
	private ProviderEntity provider;

	@Column(name = "price_amount", nullable = false)
	private BigDecimal priceAmount;

	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;

	@Column(name = "description", nullable = false)
	private String description;

	@Type(JsonType.class)
	@Column(name = "color", columnDefinition = "json", nullable = false)
	private Color color;

	@Type(JsonType.class)
	@Column(name = "sizes", columnDefinition = "json", nullable = false)
	private List<Size> sizes;

	@Column(name = "max_limit", nullable = false)
	private Long maxLimit;

	@Type(JsonType.class)
	@Column(name = "allow_config", columnDefinition = "json", nullable = false)
	private String[] allowConfig;

	@Column(name = "provided_product_file_url", nullable = false)
	private String providedProductFileUrl;

	@Column(name = "type", nullable = false)
	@Lob
	private Byte[] types;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinTable(
		name = "collection_provided_product_mapping",
		joinColumns = @JoinColumn(name = "provided_product_id"),
		inverseJoinColumns = @JoinColumn(name = "collection_id"))
	private Set<CollectionEntity> collections;
}
