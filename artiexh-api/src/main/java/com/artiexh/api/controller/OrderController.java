package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.common.Endpoint.Order;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.OrderService;
import com.artiexh.ghtk.client.model.shipfee.ShipFeeResponse;
import com.artiexh.model.domain.OrderGroup;
import com.artiexh.model.mapper.OrderGroupMapper;
import com.artiexh.model.mapper.OrderMapper;
import com.artiexh.model.rest.order.request.CheckoutRequest;
import com.artiexh.model.rest.order.request.GetShippingFeeRequest;
import com.artiexh.model.rest.order.request.PaymentQueryProperties;
import com.artiexh.model.rest.order.response.CheckoutResponse;
import com.artiexh.model.rest.order.response.PaymentResponse;
import com.artiexh.model.rest.order.response.UserOrderGroupResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Order.ROOT)
public class OrderController {

	private final OrderService orderService;
	private final OrderMapper orderMapper;
	private final OrderGroupMapper orderGroupMapper;

	@PostMapping(Endpoint.Order.CHECKOUT)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public CheckoutResponse checkout(Authentication authentication,
									 @RequestBody @Valid CheckoutRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {

			OrderGroup orderGroup = orderService.checkout(userId, request);
			return orderGroupMapper.domainToCheckoutResponse(orderGroup);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	@GetMapping(Order.SHIPPING_FEE)
	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
	public ShipFeeResponse.ShipFee getShippingFee(Authentication authentication,
												  @Valid GetShippingFeeRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {
			return orderService.getShippingFee(userId, request.getAddressId(), request);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	@GetMapping(Endpoint.Order.PAYMENT)
	public PaymentResponse payment(Authentication authentication, @PathVariable Long id, HttpServletRequest request) {
		var userId = (Long) authentication.getPrincipal();
		try {
			String ip = request.getHeader("X-FORWARDED-FOR");

			if (ip == null || ip.isEmpty()) {
				ip = request.getRemoteAddr();
			}
			return PaymentResponse.builder()
				.paymentUrl(orderService.payment(
						id,
						PaymentQueryProperties.builder()
							.vnp_IpAddr(ip)
							.build(),
						userId
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
	public ResponseEntity<Void> confirmUrl(@ParameterObject PaymentQueryProperties paymentQueryProperties,
										   RedirectAttributes attributes) {
		String confirmUrl = orderService.confirmPayment(paymentQueryProperties);
		URI uri = URI.create(confirmUrl + "/" + paymentQueryProperties.getVnp_TxnRef());
		return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
	}
}
