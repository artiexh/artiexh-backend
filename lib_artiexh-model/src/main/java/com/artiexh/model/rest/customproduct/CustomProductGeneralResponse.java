package com.artiexh.model.rest.customproduct;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@SuperBuilder
public class CustomProductGeneralResponse extends CustomProductResponse {
	private String description;
	private Integer maxItemPerOrder;
	private ProductVariant variant;

	@Setter
	@SuperBuilder
	public static class ProductVariant {
		@JsonSerialize(using = ToStringSerializer.class)
		private Long id;
		private ProductTemplateInCustomProductResponse productTemplate;
	}

}
