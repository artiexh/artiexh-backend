package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "media")
public class MediaEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "owner_id", nullable = true)
	private AccountEntity owner;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "account_media_mapping",
		joinColumns = @JoinColumn(
			name = "media_id", referencedColumnName = "id"
		),
		inverseJoinColumns = @JoinColumn(
			name = "shared_user_id", referencedColumnName = "id"
		)
	)
	private List<AccountEntity> sharedUsers;
}
