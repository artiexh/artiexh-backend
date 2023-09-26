package com.artiexh.model.rest.productvariant.response;

import com.artiexh.model.rest.productvariant.ProductVariantDetail;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantCollection {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long productBaseId;

	private Set<ProductVariantDetail> variants;
}
