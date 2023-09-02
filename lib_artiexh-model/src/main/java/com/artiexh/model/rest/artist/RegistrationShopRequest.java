package com.artiexh.model.rest.artist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationShopRequest {

	@NotEmpty
	private String shopName;

	private String shopImageUrl;

	@NotNull
	private Integer shopWardId;

	@NotEmpty
	private String shopAddress;

	@NotEmpty
	private String shopPhone;
}