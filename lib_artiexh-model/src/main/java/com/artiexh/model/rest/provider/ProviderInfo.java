package com.artiexh.model.rest.provider;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ProviderInfo {

	private String businessCode;

	private String businessName;

	private String address;

	private String contactName;

	private String email;

	private String phone;

	private String description	;

}
