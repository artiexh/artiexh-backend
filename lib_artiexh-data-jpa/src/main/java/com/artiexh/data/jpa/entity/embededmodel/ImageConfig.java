package com.artiexh.data.jpa.entity.embededmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageConfig {
	private String name;
	private Integer[] position;
	private Integer[] rotate;
	private Integer[] scale;
	private Size size;
}
