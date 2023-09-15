package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.data.jpa.entity.embededmodel.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvidedProductBase {
	private Long id;
	private ProvidedProductBaseId providedProductBaseId;

	private BigDecimal priceAmount;

	private String description;

	private Long maxLimit;

	private String providedProductFileUrl;

	private ProductBase productBase;

	private Set<VariantCombination> variantCombinations;
}
