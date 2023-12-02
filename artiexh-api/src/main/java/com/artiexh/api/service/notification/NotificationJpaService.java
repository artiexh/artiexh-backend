package com.artiexh.api.service.notification;

import com.artiexh.data.jpa.entity.NotificationEntity;
import com.artiexh.model.domain.NotificationMessage;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.notification.MessageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface NotificationJpaService {
	void markedAsRead(Long notificationId);
	PageResponse<MessageResponse> getAll(Long userId, Pageable pageable);
	void save(NotificationEntity message);
	void saveAll(Set<NotificationEntity> message);
}
