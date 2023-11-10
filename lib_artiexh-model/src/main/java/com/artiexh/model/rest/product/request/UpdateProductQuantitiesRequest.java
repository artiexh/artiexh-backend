package com.artiexh.model.rest.product.request;

import com.artiexh.model.domain.ProductHistoryAction;
import com.artiexh.model.domain.ProductInventoryQuantity;
import com.artiexh.model.domain.SourceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductQuantitiesRequest {
	private Set<ProductInventoryQuantity> productQuantities;
	private ProductHistoryAction action;
	private Long sourceId;
	private SourceCategory sourceCategory;
}
