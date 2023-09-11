package com.artiexh.model.rest.collection;

import com.artiexh.model.rest.provider.ProvidedProductBaseDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDetail {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	@NotBlank
	private String name;
	@NotBlank
	private String imageUrl;
	@JsonIgnore
	private Long artistId;
}
