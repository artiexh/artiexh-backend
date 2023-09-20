package com.artiexh.model.rest.inventory;

import com.artiexh.model.domain.Media;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemDetail {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long variantId;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@JsonIgnore
	private Long artistId;

	private Set<ImageSet> imageSet;

	private String combinationCode;

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
