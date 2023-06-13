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
	uses = {RoleMapper.class, UserStatusMapper.class, MerchMapper.class}
)
public abstract class UserMapper extends PasswordMapper {

	public abstract User entityToDomain(UserEntity userEntity);

	public abstract UserEntity domainToEntity(User user);

	@Mapping(target = "role", constant = "USER")
	@Mapping(target = "status", constant = "ACTIVE")
	@Mapping(source = "password", target = "password", qualifiedByName = "encodedPassword")
	public abstract User registerUserRequestToDomain(RegisterUserRequest request);

	@Condition
	public boolean isNotLazyLoadedSubscriptionsTo(Set<SubscriptionEntity> subscriptionsTo) {
		return Hibernate.isInitialized(subscriptionsTo);
	}

}
