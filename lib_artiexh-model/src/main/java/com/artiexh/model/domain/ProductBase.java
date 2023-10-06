package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.OptionConfig;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBase {
	private Long id;

	private LocalDateTime createdDate;

	private LocalDateTime modifiedDate;

	private String name;

	private Media modelFile;

	private List<OptionConfig> sizes;

	private String sizeDescriptionUrl;

	private String description;

	private List<ImageCombination> imageCombinations;

	private Byte model3DCode;

	private Set<ProductVariantEntity> productVariants;

	private Set<ProductOption> productOptions;

	private Set<Provider> providers;

	private ProductCategory category;

	private Set<ProductAttach> attaches;
}
