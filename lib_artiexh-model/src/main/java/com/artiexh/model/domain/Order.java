package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

	private Long id;

	private User user;

	private Shop shop;

	private UserAddress shippingAddress;

	private String note;

	private PaymentMethod paymentMethod;

	private OrderStatus status;

	private Set<OrderDetail> orderDetails;

	private LocalDateTime modifiedDate;

	private LocalDateTime createdDate;

	private OrderTransaction currentTransaction;

	private Set<OrderHistory> orderHistories;

}
