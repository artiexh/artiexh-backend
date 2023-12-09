package com.artiexh.api.service.notification.impl;

import com.artiexh.api.service.notification.PushNotificationService;
import com.artiexh.model.domain.NotificationMessage;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {
	private final SimpMessagingTemplate template;
	private final SocketIOServer server;
	private final String RECEIVE_EVENT = "messages";

//	public void sendMessage(String room, String eventName, SocketIOClient senderClient, String message) {
//		for (SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
//			if (!client.getSessionId().equals(senderClient.getSessionId())) {
//				client.sendEvent(eventName,
//					NotificationMessage.builder()
//						.title("Arty User")
//						.content("Hi, nice to meet you")
//						.build());
//			}
//		}
//	}

	@Override
	public void sendTo(NotificationMessage message) {
		for (SocketIOClient client : server.getRoomOperations(message.getOwnerId().toString()).getClients()) {
			client.sendEvent(RECEIVE_EVENT, message);
		}
	}
}
