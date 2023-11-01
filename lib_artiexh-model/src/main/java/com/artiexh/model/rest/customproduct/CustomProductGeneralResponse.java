package com.artiexh.model.rest.customproduct;

import com.artiexh.model.domain.ProductAttach;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CustomProductGeneralResponse extends CustomProductResponse {
	private String description;
	private Integer maxItemPerOrder;
	private ProductVariant variant;
	private Set<ProductAttach> attaches;

	@Data
	@SuperBuilder
	public static class ProductVariant {
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
		private ProductTemplateInCustomProductResponse productTemplate;
	}

}
