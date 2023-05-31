package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.PrinterProviderEntity;
import com.artiexh.model.domain.PrinterProvider;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {AccountMapper.class, RoleMapper.class, UserStatusMapper.class}
)
public interface PrinterProviderMapper {

	PrinterProvider entityToDomain(PrinterProviderEntity printerProviderEntity);

	PrinterProviderEntity domainToEntity(PrinterProvider printerProvider);

}
