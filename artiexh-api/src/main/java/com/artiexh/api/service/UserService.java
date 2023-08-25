package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.user.UserOrderResponse;
import com.artiexh.model.rest.user.UserOrderResponsePage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
	UserOrderResponse getOrderById(Long id, Long userId);
	PageResponse<UserOrderResponsePage> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable);
}
