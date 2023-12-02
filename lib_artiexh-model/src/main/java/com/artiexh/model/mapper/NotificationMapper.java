package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.NotificationEntity;
import com.artiexh.model.domain.NotificationMessage;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
	 NotificationEntity domainToEntity(NotificationMessage message);
}
