package com.artiexh.model.rest.producthistory;

import com.artiexh.model.domain.ProductSource;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ProductHistoryResponse extends ProductHistoryPageResponse{
	private Set<ProductHistoryDetailResponse> productHistoryDetails;
	private ProductSource productSource;
}
