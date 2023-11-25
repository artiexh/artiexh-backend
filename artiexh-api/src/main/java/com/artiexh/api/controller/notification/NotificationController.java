package com.artiexh.api.controller.notification;

import com.artiexh.model.domain.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class NotificationController {
	@MessageMapping("/application")
	@SendTo("/all/messages")
	public NotificationMessage sendAll(NotificationMessage message) {
		log.info(message.getContent());
		return message;
	}
}
