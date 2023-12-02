package com.artiexh.api.service.notification.impl;

import com.artiexh.api.service.notification.NotificationJpaService;
import com.artiexh.data.jpa.entity.NotificationEntity;
import com.artiexh.data.jpa.repository.NotificationRepository;
import com.artiexh.model.domain.NotificationMessage;
import com.artiexh.model.mapper.NotificationMapper;
import com.artiexh.model.rest.PageResponse;
import com.artiexh.model.rest.notification.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationJpaServiceImpl implements NotificationJpaService {
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;
	@Override
	public void markedAsRead(Long notificationId) {
		//TODO:
	}

	@Override
	public PageResponse<MessageResponse> getAll(Long userId, Pageable pageable) {
		//TODO
		return null;
	}

	@Override
	@Transactional
	public void save(NotificationEntity message) {
		notificationRepository.save(message);
	}

	@Override
	@Transactional
	public void saveAll(Set<NotificationEntity> messages) {
		notificationRepository.saveAll(messages);
	}
}
