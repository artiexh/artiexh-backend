package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.service.CampaignOrderService;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.order.admin.response.AdminCampaignOrderResponse;
import com.artiexh.model.rest.order.admin.response.AdminCampaignOrderResponsePage;
import com.artiexh.model.rest.order.request.OrderPageFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping(Endpoint.Admin.ROOT)
public class AdminController {
	private final CampaignOrderService campaignOrderService;

	@GetMapping(Endpoint.Admin.CAMPAIGN_ORDER)
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public PageResponse<AdminCampaignOrderResponsePage> getAllOrder(
		@ParameterObject @Valid PaginationAndSortingRequest paginationAndSortingRequest,
		@ParameterObject @Valid OrderPageFilter filter
	) {
		var campaignOrderPage = campaignOrderService.getAdminCampaignOrderInPage(
			filter.getSpecification(),
			paginationAndSortingRequest.getPageable()
		);
		return new PageResponse<>(campaignOrderPage);
	}

	@GetMapping(Endpoint.Admin.CAMPAIGN_ORDER + "/{id}")
	@PreAuthorize("hasAnyAuthority('ADMIN','STAFF')")
	public AdminCampaignOrderResponse getOrderById(@PathVariable Long id) {
		try {
			return campaignOrderService.getAdminCampaignOrderById(id);
		} catch (EntityNotFoundException exception) {
			throw new ResponseStatusException(ErrorCode.ORDER_IS_INVALID.getCode(), ErrorCode.ORDER_IS_INVALID.getMessage(), exception);
		}
	}

}
