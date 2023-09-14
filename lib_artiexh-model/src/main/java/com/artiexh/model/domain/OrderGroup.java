package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderGroup {
	private Long id;
	private User user;
	private UserAddress shippingAddress;
	private OrderTransaction currentTransaction;
	private Set<Order> orders;
	private PaymentMethod paymentMethod;
	private String deliveryAddress;
	private String deliveryWard;
	private String deliveryDistrict;
	private String deliveryProvince;
	private String deliveryCountry;
	private String deliveryTel;
	private String deliveryEmail;
}
