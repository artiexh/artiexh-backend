package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
	@JsonSerialize(using = ToStringSerializer.class)
	private Long productId;
	private Integer index;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private Set<OptionValue> optionValues;
}
