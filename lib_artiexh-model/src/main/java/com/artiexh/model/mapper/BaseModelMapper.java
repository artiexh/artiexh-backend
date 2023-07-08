package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.model.rest.basemodel.BaseModelDetail;
import com.artiexh.model.rest.basemodel.BaseModelInfo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface BaseModelMapper {
	BaseModelEntity detailToEntity(BaseModelDetail detail);

	BaseModelEntity detailToEntity(BaseModelDetail detail, @MappingTarget BaseModelEntity entity);

	BaseModelInfo entityToInfo(BaseModelEntity entity);

	BaseModelDetail entityToDetail(BaseModelEntity entity);
}
