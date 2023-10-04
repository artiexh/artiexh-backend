package com.artiexh.data.jpa.entity;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.OptionConfig;
import io.hypersistence.utils.hibernate.id.Tsid;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "product_base")
public class ProductBaseEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@OneToOne(orphanRemoval = true,
		cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "product_file_id")
	private MediaEntity modelFile;

	@Type(JsonType.class)
	@Column(name = "sizes", columnDefinition = "json", nullable = false)
	private List<OptionConfig> sizes;

	@Column(name = "size_description_url")
	private String sizeDescriptionUrl;

	@Column(name = "description", nullable = false)
	private String description;

	@Type(JsonType.class)
	@Column(name = "image_combinations", columnDefinition = "json", nullable = false)
	private Set<ImageCombination> imageCombinations;

	@Column(name = "3D_model_code")
	private Byte model3DCode;

	@Column(name = "has_variant", nullable = false)
	private boolean hasVariant;

	@OneToMany(mappedBy = "productBase", fetch = FetchType.EAGER)
	private Set<ProductVariantEntity> providedModels;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private Set<ProductOptionEntity> productOptions;

	@ManyToMany()
	@JoinTable(
		name = "product_base_provider_mapping",
		joinColumns = {@JoinColumn(name = "product_base_id")},
		inverseJoinColumns = {@JoinColumn(name = "business_code")}
	)
	private Set<ProviderEntity> providers;

	@OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "product_base_id")
	private Set<ProductAttachEntity> attaches;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.SET_NULL)
	@JoinColumn(name = "category_id")
	private ProductCategoryEntity category;
}
