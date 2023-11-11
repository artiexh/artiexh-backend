package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.CampaignOrderService;
import com.artiexh.api.service.OrderService;
import com.artiexh.model.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Order.ROOT)
public class OrderController {
	private final OrderService orderService;
	private final CampaignOrderService campaignOrderService;
	private final OrderMapper orderMapper;

//	@PostMapping(Endpoint.Order.CHECKOUT)
//	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
//	public DetailUserOrderResponse checkout(Authentication authentication,
//											@RequestBody @Valid CheckoutRequest request) {
//		var userId = (Long) authentication.getPrincipal();
//		try {
//			Order order = orderService.checkout(userId, request);
//			return orderMapper.domainToUserDetailResponse(order);
//		} catch (IllegalArgumentException ex) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
//		} catch (UnsupportedOperationException ex) {
//			throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, ex.getMessage());
//		}
//	}
//
//	@GetMapping(Endpoint.Order.SHIPPING_FEE)
//	@PreAuthorize("hasAnyAuthority('USER', 'ARTIST')")
//	public ShipFeeResponse.ShipFee getShippingFee(Authentication authentication,
//												  @ParameterObject @Valid GetShippingFeeRequest request) {
//		var userId = (Long) authentication.getPrincipal();
//		try {
//			return campaignOrderService.getShippingFee(userId, request).block();
//		} catch (ArtiexhConfigException ex) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//		} catch (IllegalArgumentException ex) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
//		}
//	}
//
//	@GetMapping(Endpoint.Order.PAYMENT)
//	public PaymentResponse payment(Authentication authentication,
//								   HttpServletRequest request,
//								   @PathVariable Long id,
//								   @RequestParam String confirmUrl
//	) {
//		var userId = (Long) authentication.getPrincipal();
//		try {
//			String ip = request.getHeader("X-FORWARDED-FOR");
//
//			if (ip == null || ip.isEmpty()) {
//				ip = request.getRemoteAddr();
//			}
//			return PaymentResponse.builder()
//				.paymentUrl(orderService.payment(
//						id,
//						PaymentQueryProperties.builder().vnp_IpAddr(ip).build(),
//						userId,
//						confirmUrl
//					)
//				)
//				.build();
//		} catch (IllegalArgumentException ex) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
//		} catch (EntityNotFoundException ex) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ORDER_NOT_FOUND.getMessage(), ex);
//		}
//	}
//
//	@GetMapping(Endpoint.Order.PAYMENT_RETURN)
//	public ResponseEntity<Void> confirmUrl(@ParameterObject PaymentQueryProperties paymentQueryProperties) {
//		var confirmUrl = orderService.confirmPayment(paymentQueryProperties);
//		URI uri = URI.create(confirmUrl + "/" + paymentQueryProperties.getVnp_TxnRef());
//		return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
//	}
//
//	@PatchMapping("/{id}/shipping")
//	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
//	public AdminCampaignOrderResponse updateOrderToShippingStatus(
//		@PathVariable Long id,
//		@RequestBody @Valid UpdateShippingOrderRequest updateShippingOrderRequest
//	) {
//		try {
//			return campaignOrderService.updateShippingOrderStatus(id, updateShippingOrderRequest);
//		} catch (EntityNotFoundException exception) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorCode.ORDER_NOT_FOUND.getMessage(), exception);
//		} catch (IllegalArgumentException exception) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
//		} catch (ArtiexhConfigException ex) {
//			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//		}
//	}
//
//	@PatchMapping("/{id}/status")
//	public ResponseEntity<Void> updateStatus(
//		Authentication authentication,
//		@RequestBody @Valid UpdateOrderStatusRequest request,
//		@PathVariable Long id
//	) {
//		try {
//			var userId = (Long) authentication.getPrincipal();
//			switch (request.getStatus()) {
//				case CANCELED -> campaignOrderService.cancelOrder(id, request.getMessage(), userId);
//				case REFUNDED -> {
//					boolean isAdmin = authentication.getAuthorities().stream()
//						.anyMatch(r -> r.getAuthority().equals(Role.ADMIN.name()) || r.getAuthority().equals(Role.STAFF.name()));
//					if (!isAdmin) {
//						throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Admin or Staff can update order's status REFUNDED");
//					}
//					campaignOrderService.refundOrder(id, userId);
//				}
//				default ->
//					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can just update order's status REFUNDED or CANCELED ");
//			}
//			return ResponseEntity.ok().build();
//		} catch (IllegalArgumentException ex) {
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
//		} catch (EntityNotFoundException ex) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode.ORDER_NOT_FOUND.getMessage(), ex);
//		} catch (IllegalAccessException ex) {
//			throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage(), ex);
//		}
//	}
}
