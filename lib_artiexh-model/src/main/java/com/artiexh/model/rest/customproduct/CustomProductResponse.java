package com.artiexh.model.rest.customproduct;

import com.artiexh.model.domain.Media;
import com.artiexh.model.domain.ProductCategory;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Set;

@Data
@SuperBuilder
public class CustomProductResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private String name;

	private Long campaignLock;

	private Set<String> tags;

	private Media modelThumbnail;

	private ProductCategory category;

	private Instant createdDate;

	private Instant modifiedDate;
}
