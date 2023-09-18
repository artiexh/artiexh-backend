package com.artiexh.model.rest.option;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionDetail {
	private Long id;

	private Long productBaseId;

	private String name;

	private Integer index;

	private Set<OptionValueDetail> optionValues;
}
