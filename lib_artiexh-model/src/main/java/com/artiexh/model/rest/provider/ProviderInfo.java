package com.artiexh.model.rest.provider;

import com.artiexh.model.domain.ProvidedProductBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderInfo {
	private String businessCode;

	private String businessName;

	private String address;

	private String contactName;

	private String email;

	private String phone;
}
