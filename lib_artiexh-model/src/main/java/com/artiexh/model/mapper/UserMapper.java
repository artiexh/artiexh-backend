package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.SubscriptionEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.model.domain.User;
import com.artiexh.model.rest.auth.RegisterUserRequest;
import org.hibernate.Hibernate;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {AccountMapper.class, ProductMapper.class, ProductAttachMapper.class, PasswordMapper.class}
)
public interface UserMapper {

	User entityToDomain(UserEntity userEntity);

	UserEntity domainToEntity(User user);

	@Mapping(target = "role", constant = "USER")
	@Mapping(target = "status", constant = "ACTIVE")
	@Mapping(source = "password", target = "password", qualifiedByName = "encodedPassword")
	User registerUserRequestToDomain(RegisterUserRequest request);

	@Condition
	default boolean isNotLazyLoadedSubscriptionsTo(Set<SubscriptionEntity> subscriptionsTo) {
		return Hibernate.isInitialized(subscriptionsTo);
	}

}
