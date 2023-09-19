package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.artiexh.data.jpa.entity.ProductOptionTemplateEntity;
import com.artiexh.data.jpa.entity.embededmodel.OptionConfig;
import com.artiexh.data.opensearch.model.OptionTemplateDocument;
import com.artiexh.model.domain.OptionValue;
import com.artiexh.model.domain.ProductOption;
import com.artiexh.model.rest.option.OptionDetail;
import com.artiexh.model.rest.option.OptionValueDetail;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductOptionMapper {
	ProductOption entityToDomain(ProductOptionEntity entity);

	ProductOption entityToDomain(ProductOptionTemplateEntity entity);

	ProductOption optionConfigToProductOption(OptionConfig optionConfig);

	OptionDetail domainToDetail(ProductOption productOption);

	OptionValueDetail domainToDetail(OptionValue optionValue);

	ProductOption documentToDomain(OptionTemplateDocument document);
}
