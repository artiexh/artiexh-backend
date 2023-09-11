package com.artiexh.model.rest.collection.request;

import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionRequest {
	@NotNull
	Set<Long> providedProductIds;
	@NotBlank
	private String name;
	@NotBlank
	private String imageUrl;
}
