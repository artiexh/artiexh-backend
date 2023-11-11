package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.CampaignOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Admin.ROOT)
public class AdminController {
	private final CampaignOrderService campaignOrderService;

//	@GetMapping(Endpoint.Admin.CAMPAIGN_ORDER)
//	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
//	public PageResponse<CampaignOrderResponsePage> getAllOrder(
//		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
//		@ParameterObject @Valid OrderPageFilter filter
//	) {
//		var campaignOrderPage = campaignOrderService.getAdminCampaignOrderInPage(
//			filter.getSpecification(),
//			paginationAndSortingRequest.getPageable()
//		);
//		return new PageResponse<>(campaignOrderPage);
//	}
//
//	@GetMapping(Endpoint.Admin.CAMPAIGN_ORDER + "/{id}")
//	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
//	public AdminCampaignOrderResponse getOrderById(@PathVariable Long id) {
//		try {
//			return campaignOrderService.getAdminCampaignOrderById(id);
//		} catch (EntityNotFoundException exception) {
//			throw new ResponseStatusException(ErrorCode.ORDER_IS_INVALID.getCode(), ErrorCode.ORDER_IS_INVALID.getMessage(), exception);
//		}
//	}

}
