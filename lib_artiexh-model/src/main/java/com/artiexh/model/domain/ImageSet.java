package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageSet {
	private Long id;

	private Media mockupImage;

	private Media manufacturingImage;

	private String positionCode;
}
