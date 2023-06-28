package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.PrinterProviderEntity;
import com.artiexh.model.domain.PrinterProvider;
import com.artiexh.model.rest.auth.RegisterPrinterProviderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {AccountMapper.class, PasswordMapper.class}
)
public interface PrinterProviderMapper {

	PrinterProvider entityToDomain(PrinterProviderEntity printerProviderEntity);

	PrinterProviderEntity domainToEntity(PrinterProvider printerProvider);

	@Mapping(target = "role", constant = "PRINTER_PROVIDER")
	@Mapping(target = "status", constant = "ACTIVE")
	@Mapping(target = "password", source = "password", qualifiedByName = "encodedPassword")
	PrinterProvider registerPrinterProviderRequestToDomain(RegisterPrinterProviderRequest request);

}
