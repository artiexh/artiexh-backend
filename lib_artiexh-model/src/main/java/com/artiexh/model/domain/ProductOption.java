package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductOption {
	private String name;
	private Long productId;
	private Integer index;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	private Set<OptionValue> optionValues;
}
