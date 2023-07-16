package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
	uses = {
		ProvidedProductMapper.class
	}
)
public interface ProviderMapper {
	ProviderEntity detailToEntity(ProviderDetail detail);

	ProviderEntity detailToEntity(ProviderDetail detail, @MappingTarget ProviderEntity entity);

	ProviderInfo entityToInfo(ProviderEntity entity);

	ProviderDetail entityToDetail(ProviderEntity entity);
}
