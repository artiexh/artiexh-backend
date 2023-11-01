package com.artiexh.model.rest.customproduct;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductTemplateInCustomProductResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String name;
}
