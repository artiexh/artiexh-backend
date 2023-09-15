package com.artiexh.model.rest.productbase;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.data.jpa.entity.embededmodel.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseDetail extends ProductBaseInfo{

	private String description;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<ProviderEntity> providers;
}
