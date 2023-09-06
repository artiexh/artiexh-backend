package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.data.jpa.entity.OrderGroupEntity;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.user.UserOrderGroupResponse;
import com.artiexh.model.rest.user.UserOrderGroupResponsePage;
import com.artiexh.model.rest.user.UserOrderResponse;
import com.artiexh.model.rest.user.UserOrderResponsePage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {

	UserOrderGroupResponse getOrderGroupById(Long id, Long userId);

	PageResponse<UserOrderGroupResponsePage> getOrderGroupInPage(Specification<OrderGroupEntity> specification, Pageable pageable);

	Page<UserOrderResponsePage> getOrderInPage(Specification<OrderEntity> specification, Pageable pageable);

	UserOrderResponse getOrderById(Long id, Long userId);

}
