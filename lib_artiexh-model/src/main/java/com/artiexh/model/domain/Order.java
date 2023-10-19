package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
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
	private Instant modifiedDate;
	private Instant createdDate;
	private List<OrderHistory> orderHistories;
	private String shippingLabel;
	private Long orderId;
	private String pickAddress;
	private String pickWard;
	private String pickDistrict;
	private String pickProvince;
	private String pickCountry;
	private String pickTel;
	private String pickName;
	private String pickEmail;
	private String returnAddress;
	private String returnWard;
	private String returnDistrict;
	private String returnProvince;
	private String returnCountry;
	private String returnTel;
	private String returnName;
	private String returnEmail;
}
