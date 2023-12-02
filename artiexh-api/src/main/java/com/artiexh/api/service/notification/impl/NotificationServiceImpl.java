package com.artiexh.api.service.notification.impl;

import com.artiexh.api.service.notification.NotificationJpaService;
import com.artiexh.api.service.notification.NotificationService;
import com.artiexh.api.service.notification.PushNotificationService;
import com.artiexh.data.jpa.entity.NotificationEntity;
import com.artiexh.data.jpa.repository.AccountRepository;
import com.artiexh.model.domain.NotificationMessage;
import com.artiexh.model.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
	private final NotificationJpaService notificationJpaService;
	private final AccountRepository accountRepository;
	private final PushNotificationService pushNotificationService;
	private final NotificationMapper notificationMapper;
	@Override
	public void sendAll(NotificationMessage message) {
		Set<NotificationEntity> notifications = accountRepository.findAll().stream()
			.map(account -> NotificationEntity.builder()
				.content(message.getContent())
				.title(message.getTitle())
				.owner(account)
				.build())
			.collect(Collectors.toSet());
		notificationJpaService.saveAll(notifications);
		pushNotificationService.sendAll(message);
	}

	@Override
	public void sendTo(Long userId, NotificationMessage message) {
		notificationJpaService.save(notificationMapper.domainToEntity(message));
		pushNotificationService.sendTo(userId, message);
	}
}
