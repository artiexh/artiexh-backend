package com.artiexh.api.service.notification;

import com.artiexh.model.domain.NotificationMessage;
import com.artiexh.model.rest.notification.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
	void sendAll(NotificationMessage message);
	void sendTo(Long userId, NotificationMessage message);
	void sendTo(List<Long> userId, NotificationMessage message);
	Page<MessageResponse> getAll(Long userId, Pageable pageable);
	void markedAsRead(Long userId, Long notificationId);
}
