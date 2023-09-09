package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

	private Long id;

	private User user;

	private UserAddress shippingAddress;

	private Shop shop;

	private BigDecimal shippingFee;

	private String note;

	private PaymentMethod paymentMethod;

	private OrderStatus status;

	private Set<OrderDetail> orderDetails;

	private LocalDateTime modifiedDate;

	private LocalDateTime createdDate;

	private List<OrderHistory> orderHistories;

	private String shippingLabel;

	private Long orderId;

}
