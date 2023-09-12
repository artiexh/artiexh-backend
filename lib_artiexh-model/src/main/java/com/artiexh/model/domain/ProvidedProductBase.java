package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.data.jpa.entity.embededmodel.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvidedProductBase {
	private Long id;
	private ProvidedProductBaseId providedProductBaseId;

	private BigDecimal priceAmount;

	private String priceUnit;

	private String description;

	private ImageCombination imageCombination;

	private List<Size> sizes;

	private Long maxLimit;

	private String[] allowConfig;

	private String providedProductFileUrl;

	private ProductBase productBase;

	private ProvidedProductType[] types;
}
