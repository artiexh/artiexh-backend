package com.artiexh.model.rest.user;

import com.artiexh.model.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderResponsePage {
	private Long id;

	private Shop shop;

	private UserAddress shippingAddress;

	private String note;

	private PaymentMethod paymentMethod;

	private OrderStatus status;

	private LocalDateTime modifiedDate;

	private LocalDateTime createdDate;
}
