package com.artiexh.api.controller.notification;

import com.artiexh.model.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MessageController {
	private final SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/application")
	@SendTo("/all/messages")
	public NotificationMessage sendAll(NotificationMessage message) {
		log.info(message.getContent());
		return message;
	}

	@MessageMapping("/private")
	public void sendToSpecificUser(NotificationMessage message) {
		simpMessagingTemplate.convertAndSendToUser(message.getOwnerId().toString(), "/specific", message);
	}
}
