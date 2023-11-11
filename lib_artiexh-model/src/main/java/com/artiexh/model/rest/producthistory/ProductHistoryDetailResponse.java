package com.artiexh.model.rest.producthistory;

import com.artiexh.model.domain.ProductHistoryAction;
import com.artiexh.model.domain.ProductHistoryDetail;
import com.artiexh.model.domain.SourceCategory;
import com.artiexh.model.rest.product.response.ProductResponse;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductHistoryDetailResponse {
	private ProductResponse productInventory;
	private Long quantity;
}
