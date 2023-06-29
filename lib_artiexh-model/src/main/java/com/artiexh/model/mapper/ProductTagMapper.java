package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductTagEntity;
import com.artiexh.model.domain.ProductTag;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductTagMapper {

	ProductTag entityToDomain(ProductTagEntity productTagEntity);

	ProductTagEntity domainToEntity(ProductTag productTag);

	default String tagDomainToTagResponse(ProductTag tag) {
		if (tag == null) {
			return null;
		}
		return tag.getName();
	}

	default ProductTag tagResponseToTagDomain(String tag) {
		if (tag == null) {
			return null;
		}
		ProductTag productTag = new ProductTag();
		productTag.setName(tag);
		return productTag;
	}

}
