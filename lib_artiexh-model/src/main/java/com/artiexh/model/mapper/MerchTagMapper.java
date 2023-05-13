package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchTagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MerchTagMapper {

    default String entityToDomain(MerchTagEntity merchTagEntity) {
        return merchTagEntity.getName();
    }

    default MerchTagEntity domainToEntity(String merchTag) {
        return MerchTagEntity.builder().name(merchTag).build();
    }

}
