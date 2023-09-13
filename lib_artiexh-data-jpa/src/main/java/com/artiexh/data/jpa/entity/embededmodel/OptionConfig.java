package com.artiexh.data.jpa.entity.embededmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionConfig {
	private String key;
	private String label;
	private String value;
	private Integer index;
}
