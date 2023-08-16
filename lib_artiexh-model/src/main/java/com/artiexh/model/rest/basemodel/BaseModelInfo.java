package com.artiexh.model.rest.basemodel;

import com.artiexh.model.domain.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseModelInfo {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	@NotEmpty
	private String name;

	@NotEmpty
	private String type;

	@NotEmpty
	private String modelFileUrl;

	@NotEmpty
	private List<Size> sizes;
}
