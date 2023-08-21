package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductTagEntity;
import com.artiexh.model.domain.ProductTag;
import com.artiexh.model.rest.tag.ProductTagResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.HashSet;
import java.util.Set;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductTagMapper {

    ProductTag entityToDomain(ProductTagEntity productTagEntity);

    ProductTagEntity domainToEntity(ProductTag productTag);

    ProductTagResponse entityToResponse(ProductTagEntity productTag);

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

    default Set<ProductTag> tagNamesToTagDomainS(Set<String> tag) {
        Set<ProductTag> result = new HashSet<>();

        if (tag != null) {
            for (String name : tag) {
                result.add(tagResponseToTagDomain(name));
            }
        }

        return result;
    }

}
