package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.AccountEntity;
import com.artiexh.model.domain.Account;
import com.artiexh.model.request.RegisterAdminRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserStatusMapper.class, RoleMapper.class}
)
public abstract class AccountMapper extends PasswordMapper {

	public abstract Account entityToDomain(AccountEntity accountEntity);

	public abstract AccountEntity domainToEntity(Account account);

	@Mapping(target = "role", constant = "ADMIN")
	@Mapping(target = "status", constant = "ACTIVE")
	@Mapping(source = "password", target = "password", qualifiedByName = "encodedPassword")
	public abstract Account registerAdminRequestToDomain(RegisterAdminRequest registerAdminRequest);

}