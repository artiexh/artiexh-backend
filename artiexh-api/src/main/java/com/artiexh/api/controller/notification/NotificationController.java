package com.artiexh.api.controller.notification;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.base.exception.IllegalAccessException;
import com.artiexh.api.service.notification.NotificationService;
import com.artiexh.data.jpa.repository.CampaignOrderRepository;
import com.artiexh.model.domain.NotificationMessage;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.PaginationAndSortingRequest;
import com.artiexh.model.rest.notification.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = Endpoint.Notification.ROOT)
public class NotificationController {
	private final NotificationService notificationService;

	@PostMapping("/test/push-noti")
	public void getAllMessages() {

		Long ownerId = 456143215578222903L;
		notificationService.sendTo(ownerId, NotificationMessage.builder()
			.ownerId(ownerId)
			.title("Cập nhật trạng thái chiến dịch")
			.content("Đơn hàng " + 123 +  " đã được vận chuyển")
			.build());
	}

	@GetMapping()
	public PageResponse<MessageResponse> getAllMessages(
		Authentication authentication,
		@ParameterObject @Valid PaginationAndSortingRequest pagination) {
		long userId = (long) authentication.getPrincipal();
		return new PageResponse<>(notificationService.getAll(userId, pagination.getPageable()));
	}

	@PostMapping("/{id}")
	public void markedAsRead(
		@PathVariable("id") Long id,
		Authentication authentication) {
		long userId = (long) authentication.getPrincipal();
		try {
			notificationService.markedAsRead(userId, id);
		} catch (IllegalAccessException exception) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}
}
