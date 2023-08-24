package com.artiexh.model.rest.artist;

import com.artiexh.model.domain.OrderDetail;
import com.artiexh.model.rest.order.response.OrderDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopOrderResponse extends ShopOrderResponsePage{
	private Set<OrderDetailResponse> orderDetails;
}
