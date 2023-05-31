package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.AccountEntity;
import com.artiexh.model.domain.Account;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserStatusMapper.class, RoleMapper.class}
)
public interface AccountMapper {

	Account entityToDomain(AccountEntity accountEntity);

	AccountEntity domainToEntity(Account account);

}
