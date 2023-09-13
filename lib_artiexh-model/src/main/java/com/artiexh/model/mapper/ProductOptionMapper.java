package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.artiexh.data.jpa.entity.embededmodel.OptionConfig;
import com.artiexh.model.domain.ProductOption;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductOptionMapper {
	ProductOption entityToDomain(ProductOptionEntity entity);

	ProductOption optionConfigToProductOption(OptionConfig optionConfig);
}
