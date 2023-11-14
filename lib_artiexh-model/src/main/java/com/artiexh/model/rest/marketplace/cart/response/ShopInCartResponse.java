package com.artiexh.model.rest.marketplace.cart.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopInCartResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private String shopName;
	private String imageUrl;
	private ShopOwnerResponse owner;
}
