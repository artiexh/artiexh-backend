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
public class CustomProductGeneralRequest {

	private Long id;

	@NotNull
	private Long variantId;

	@NotBlank
	@Size(max = 50)
	private String name;

	@JsonIgnore
	private Long artistId;

	private String description;

	private Set<String> tags = Set.of();

	private Long modelThumbnailId;

	private Integer maxItemPerOrder;
}
