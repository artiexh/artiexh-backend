package com.artiexh.model.rest.artist.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationShopRequest {
	@NotEmpty
	private String shopName;
}
