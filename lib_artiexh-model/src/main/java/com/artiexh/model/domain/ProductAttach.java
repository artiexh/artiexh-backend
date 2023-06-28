package com.artiexh.model.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttach {
	private Long id;

	@NotBlank
	private String url;

	@NotBlank
	private ProductAttachType type;


	private String title;

	private String description;
}
