package com.artiexh.model.rest.productbase;

import com.artiexh.data.jpa.entity.embededmodel.ImageCombination;
import com.artiexh.data.jpa.entity.embededmodel.OptionConfig;
import com.artiexh.model.domain.Media;
import com.artiexh.model.domain.Model3DCode;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.domain.ProductOption;
import com.artiexh.model.rest.category.ProductCategoryResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseInfo {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotBlank
	private String name;

	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long modelFileId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(allOf = Media.class)
	private Media modelFile;

	@NotNull
	@Valid
	private Set<ProductOption> productOptions;

	@NotEmpty
	private Set<ProductAttach> attaches;

	@NotNull
	private List<OptionConfig> sizes;

	private String sizeDescriptionUrl;

	@NotNull
	private List<ImageCombination> imageCombinations;

	@NotNull
	private Model3DCode model3DCode;

	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long categoryId;

	@NotEmpty
	private Set<String> businessCodes;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(allOf = ProductCategoryResponse.class)
	private ProductCategoryResponse category;
}
