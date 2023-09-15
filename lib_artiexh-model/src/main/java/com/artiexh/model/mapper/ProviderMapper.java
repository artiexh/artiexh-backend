package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductVariantMapper.class, ProductBaseMapper.class}
)
public interface ProviderMapper {
	Provider entityToDomain(ProviderEntity entity);

	@Mapping(target = "productBases", source = "productBaseIds")
	Provider detailToDomain(ProviderDetail detail);

	ProviderEntity domainToEntity(Provider domain);

	ProviderDetail domainToDetail(Provider domain);

	ProviderInfo domainToInfo(Provider domain);
}
