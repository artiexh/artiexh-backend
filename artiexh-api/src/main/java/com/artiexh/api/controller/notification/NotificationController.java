package com.artiexh.api.controller.notification;

import com.artiexh.model.domain.NotificationMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
	@MessageMapping("/application")
	@SendTo("/all/messages")
	public NotificationMessage sendAll(NotificationMessage message) {
		return message;
	}
}
