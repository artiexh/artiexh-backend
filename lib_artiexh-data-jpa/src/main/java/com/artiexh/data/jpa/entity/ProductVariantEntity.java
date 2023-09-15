package com.artiexh.data.jpa.entity;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.Size;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provided_product")
@Builder(toBuilder = true)
public class ProductVariantEntity {
	@Id
	@Tsid
	private Long id;

	@NotNull
	@Column(name = "business_code", nullable = false)
	private String businessCode;

	@NotNull
	@Column(name = "product_base_id", nullable = false)
	private Long productBaseId;

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

	@Column(name = "max_limit", nullable = false)
	private Integer maxLimit;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "provided_product_file_url", nullable = false)
	private String providedProductFileUrl;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "variant_id")
	private Set<ProductVariantCombinationEntity> variantCombinations;
}
