package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.common.Endpoint.Order;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.OrderGroupService;
import com.artiexh.api.service.OrderService;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.model.domain.OrderGroup;
import com.artiexh.model.domain.Role;
import com.artiexh.model.mapper.OrderGroupMapper;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import com.artiexh.model.rest.order.request.PaymentQueryProperties;
import com.artiexh.model.rest.order.request.UpdateStatusRequest;
import com.artiexh.model.rest.order.response.PaymentResponse;
import com.artiexh.model.rest.user.UserOrderGroupResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Order.ROOT)
public class OrderController {

	private final OrderService orderService;
	private final OrderGroupService orderGroupService;
	private final OrderGroupMapper orderGroupMapper;
	private final StringRedisTemplate redisTemplate;

	@PostMapping(Endpoint.Order.CHECKOUT)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public UserOrderGroupResponse checkout(Authentication authentication,
										   @RequestBody @Valid CheckoutRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {

			OrderGroup orderGroup = orderGroupService.checkout(userId, request);
			return orderGroupMapper.domainToUserResponse(orderGroup);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	@GetMapping(Order.SHIPPING_FEE)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public ShipFeeResponse.ShipFee getShippingFee(Authentication authentication,
												  @ParameterObject @Valid GetShippingFeeRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {
			return orderService.getShippingFee(userId, request.getAddressId(), request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	@GetMapping(Endpoint.Order.PAYMENT)
	public PaymentResponse payment(Authentication authentication,
								   HttpServletRequest request,
								   @PathVariable Long id,
								   @RequestParam String confirmUrl
	) {
		var userId = (Long) authentication.getPrincipal();
		try {
			String ip = request.getHeader("X-FORWARDED-FOR");

			if (ip == null || ip.isEmpty()) {
				ip = request.getRemoteAddr();
			}
			return PaymentResponse.builder()
				.paymentUrl(orderGroupService.payment(
						id,
						PaymentQueryProperties.builder().vnp_IpAddr(ip).build(),
						userId,
						confirmUrl
					)
				)
				.build();
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ORDER_NOT_FOUND.getMessage(), ex);
		}
	}

	@GetMapping(Endpoint.Order.PAYMENT_RETURN)
	public ResponseEntity<Void> confirmUrl(@ParameterObject PaymentQueryProperties paymentQueryProperties) {
		orderGroupService.confirmPayment(paymentQueryProperties);
		String confirmUrl =
			redisTemplate.boundValueOps("payment_confirm_url_" + paymentQueryProperties.getVnp_TxnRef()).getAndDelete();
		URI uri = URI.create(confirmUrl + "/" + paymentQueryProperties.getVnp_TxnRef());
		return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> updateStatus(
		Authentication authentication,
		@RequestBody UpdateStatusRequest request,
		@PathVariable Long id
	) {
		try {
			var userId = (Long) authentication.getPrincipal();
			switch (request.getStatus()) {
				case CANCELED -> orderService.cancelOrder(id, request.getMessage(), userId);
				case REFUNDED -> {
					boolean isAdmin = authentication.getAuthorities().stream()
						.anyMatch(r -> r.getAuthority().equals(Role.ADMIN.name()) || r.getAuthority().equals(Role.STAFF.name()));
					if (!isAdmin) {
						throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Admin or Staff can update order's status REFUNDED");
					}
					orderService.refundOrder(id, userId);
				}
				default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can just update order's status REFUNDED or CANCELED ");
			}
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().equals(ErrorCode.ORDER_STATUS_NOT_ALLOWED.name())) {
				throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorCode.ORDER_STATUS_NOT_ALLOWED.getMessage());
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		} catch (EntityNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ORDER_NOT_FOUND.getMessage(), ex);
		}

	}
}
