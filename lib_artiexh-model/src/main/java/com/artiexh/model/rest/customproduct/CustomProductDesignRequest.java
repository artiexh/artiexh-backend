package com.artiexh.model.rest.customproduct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomProductDesignRequest {

	private Long id;

	@NotNull
	private Long variantId;

	@NotBlank
	@Size(max = 50)
	private String name;

	@JsonIgnore
	private Long artistId;

	private Set<ImageSet> imageSet;

	private String combinationCode;

	private Long modelThumbnailId;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ImageSet {
		private Long mockupImageId;
		private Long manufacturingImageId;
		private String positionCode;
	}
}
