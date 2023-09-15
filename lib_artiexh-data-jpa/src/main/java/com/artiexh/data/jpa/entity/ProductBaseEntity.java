package com.artiexh.data.jpa.entity;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.OptionConfig;
import io.hypersistence.utils.hibernate.id.Tsid;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
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

//	@Column(name = "type", nullable = false)
//	private String type;

	@Column(name = "product_file_url", nullable = false)
	private String productFileUrl;

	@Type(JsonType.class)
	@Column(name = "sizes", columnDefinition = "json", nullable = false)
	private List<OptionConfig> sizes;

	@Column(name = "description", nullable = false)
	private String description;

	@Type(JsonType.class)
	@Column(name = "image_combinations", columnDefinition = "json", nullable = false)
	private List<ImageCombination> imageCombinations;

	@Column(name = "3D_model_code")
	private Byte model3DCode;

	@OneToMany(mappedBy = "productBase", fetch = FetchType.EAGER)
	private Set<ProductVariantEntity> providedModels;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private Set<ProductOptionEntity> productOptions;

	@ManyToMany(mappedBy = "productBases")
	private Set<ProviderEntity> providers;
}
