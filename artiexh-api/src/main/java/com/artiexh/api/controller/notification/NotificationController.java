package com.artiexh.api.controller.notification;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.service.notification.NotificationService;
import com.artiexh.model.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = Endpoint.Notification.ROOT)
public class NotificationController {
	private final NotificationService notificationService;
//	@MessageMapping("/application")
//	@SendTo("/all/messages")
//	public NotificationMessage sendAll(NotificationMessage message) {
//		log.info(message.getContent());
//		return message;
//	}
//
//	@MessageMapping("/private")
//	public void sendToSpecificUser(NotificationMessage message) {
//		//simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/specific", message);
//	}

	@PostMapping("/test")
	public void sendAll() {
		notificationService.sendAll(NotificationMessage.builder()
			.content("Hell to all users")
			.title("Arty System")
			.build());
	}

	@PostMapping("/test/{user-id}")
	public void sendAll(@PathVariable("user-id") Long userId) {
		notificationService.sendTo(userId, NotificationMessage.builder()
			.content("Hell to user " + userId)
				.ownerId(userId)
			.title("Arty System")
			.build());
	}
}
