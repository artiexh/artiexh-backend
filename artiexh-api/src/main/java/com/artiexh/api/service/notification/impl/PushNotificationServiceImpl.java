package com.artiexh.api.service.notification.impl;

import com.artiexh.api.service.notification.PushNotificationService;
import com.artiexh.model.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {
	private final SimpMessagingTemplate template;
	@Override
	public void sendAll(NotificationMessage message) {
		log.info("Send notification to application with message " + message);
		template.convertAndSend("/all/messages", message);
	}

	@Override
	public void sendTo(Long userId, NotificationMessage message) {
		log.info("Send notification to user " + userId + " with message " + message);
		template.convertAndSendToUser(userId.toString(), "specific", message);
	}
}
