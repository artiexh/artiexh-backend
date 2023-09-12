package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OptionValue {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	private String name;
	private Long optionId;
}
