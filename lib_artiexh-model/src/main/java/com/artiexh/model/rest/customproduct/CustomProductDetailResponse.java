package com.artiexh.model.rest.customproduct;

import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.domain.ProductAttach;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CustomProductDetailResponse extends CustomProductResponse{

	private String description;
	private Integer maxItemPerOrder;
	private Set<ProductAttach> attaches;
	private Set<ImageSet> imageSet;
	private String combinationCode;
}
