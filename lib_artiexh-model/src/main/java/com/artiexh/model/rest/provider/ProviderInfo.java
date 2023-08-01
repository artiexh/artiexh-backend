package com.artiexh.model.rest.provider;

import com.artiexh.model.validation.BusinessCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

	@BusinessCode
	private String businessCode;

	@NotBlank
	private String businessName;

	@NotBlank
	private String address;

	@NotBlank
	private String contactName;

	@NotBlank
	@Email
	private String email;

	//	@Pattern(regexp = "^[0-9]{10,15}$",
//		message = "Phone must be of 10 to 15 length with digits characters")
	@Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$",
		message = "Phone must be based on ITU-T standards. Ex: +84 382913108")
	private String phone;

	private String description;

}
