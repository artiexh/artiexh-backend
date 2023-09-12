package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.OptionConfig;
import com.artiexh.data.jpa.entity.embededmodel.Size;
import io.hypersistence.utils.hibernate.id.Tsid;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBase {
	private Long id;

	private String name;

	private String productFileUrl;

	private List<OptionConfig> sizes;

	private String description;

	private List<ImageCombination> imageCombinations;

	private Byte model3DCode;

	private Set<ProvidedProductBaseEntity> providedModels;

	private Set<ProductOption> productOptions;
}
