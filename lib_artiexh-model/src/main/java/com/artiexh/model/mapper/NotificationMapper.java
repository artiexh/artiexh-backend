package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.NotificationEntity;
import com.artiexh.model.domain.NotificationMessage;
import com.artiexh.model.rest.notification.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
	@Mapping(target = "owner.id", source = "ownerId")
	 NotificationEntity domainToEntity(NotificationMessage message);

	MessageResponse domainToResponse(NotificationEntity notification);
}
