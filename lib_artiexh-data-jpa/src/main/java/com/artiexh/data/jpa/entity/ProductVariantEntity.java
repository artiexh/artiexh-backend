package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_variant")
@Builder(toBuilder = true)
public class ProductVariantEntity {
	@Id
	@Tsid
	private Long id;

	@NotNull
	@Column(name = "product_base_id", nullable = false)
	private Long productBaseId;

	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "product_base_id", insertable = false, updatable = false)
	private ProductBaseEntity productBase;

	@Column(name = "max_limit", nullable = false)
	private Integer maxLimit;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "provided_product_file_url", nullable = false)
	private String providedProductFileUrl;

	@OneToMany(mappedBy = "productVariant",
		orphanRemoval = true,
		cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private Set<ProductVariantCombinationEntity> variantCombinations = new HashSet<>();

	@OneToMany(mappedBy = "productVariant",
		orphanRemoval = true,
		cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	private Set<ProductVariantProviderEntity> providerConfigs = new HashSet<>();
}
