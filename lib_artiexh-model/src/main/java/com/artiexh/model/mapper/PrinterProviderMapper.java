package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.PrinterProviderEntity;
import com.artiexh.model.domain.PrinterProvider;
import com.artiexh.model.request.RegisterPrinterProviderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {AccountMapper.class, RoleMapper.class, UserStatusMapper.class}
)
public interface PrinterProviderMapper {

	PrinterProvider entityToDomain(PrinterProviderEntity printerProviderEntity);

	PrinterProviderEntity domainToEntity(PrinterProvider printerProvider);

	@Mapping(target = "role", constant = "PRINTER_PROVIDER")
	@Mapping(target = "status", constant = "ACTIVE")
	@Mapping(target = "password", expression = "java(passwordEncoder.encode(request.password()))", ignore = true)
	PrinterProvider registerPrinterProviderRequestToDomain(RegisterPrinterProviderRequest request);

}
