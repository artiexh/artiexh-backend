package com.artiexh.api.service.notification;

import com.artiexh.model.domain.NotificationMessage;

public interface NotificationService {
	void sendAll(NotificationMessage message);
	void sendTo(Long userId, NotificationMessage message);
}
