package com.artiexh.model.rest.customproduct;

import com.artiexh.model.domain.Media;
import com.artiexh.model.rest.productvariant.ProductVariantDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomProductDetail {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long variantId;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@NotBlank
	@Size(max = 50)
	private String name;

	@JsonIgnore
	private Long artistId;

	private Set<ImageSet> imageSet;

	private String combinationCode;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(allOf = ProductVariantDetail.class)
	private ProductVariantDetail variant;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long campaignLock;

	private String description;

	private Set<String> tags = Set.of();

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant createdDate;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Instant modifiedDate;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long thumbnailId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(allOf = Media.class)
	private Media thumbnail;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ImageSet {
		@JsonSerialize(using = ToStringSerializer.class)
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private Long mockupImageId;

		@JsonSerialize(using = ToStringSerializer.class)
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private Long manufacturingImageId;

		@JsonProperty(access = JsonProperty.Access.READ_ONLY)
		@Schema(allOf = Media.class)
		private Media mockupImage;

		@JsonProperty(access = JsonProperty.Access.READ_ONLY)
		@Schema(allOf = Media.class)
		private Media manufacturingImage;

		private String positionCode;
	}
}
