package com.artiexh.model.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInventoryQuantity {
	private String productCode;
	private Long quantity;
}
