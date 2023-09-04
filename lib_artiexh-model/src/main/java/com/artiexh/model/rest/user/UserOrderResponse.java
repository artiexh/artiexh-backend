package com.artiexh.model.rest.user;

import com.artiexh.model.domain.OrderDetail;
import com.artiexh.model.rest.order.response.OrderDetailResponse;
import com.artiexh.model.rest.transaction.OrderTransactionResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderResponse extends UserOrderResponsePage {
	private Set<OrderDetailResponse> orderDetails;
}
