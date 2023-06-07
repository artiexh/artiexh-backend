package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchAttach {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	private String url;
	private MerchAttachType type;
	private String title;
	private String description;
}
