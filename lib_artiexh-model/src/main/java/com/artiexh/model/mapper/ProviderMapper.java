package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProvidedProductBaseMapper.class}
)
public interface ProviderMapper {
	Provider entityToDomain(ProviderEntity entity);

	Provider detailToDomain(ProviderDetail detail);

	ProviderEntity domainToEntity(Provider domain);

	ProviderDetail domainToDetail(Provider domain);

	ProviderInfo domainToInfo(Provider domain);
}
