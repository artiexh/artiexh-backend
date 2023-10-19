package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campaign")
public class CampaignEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@NotNull
	@Column(name = "status", nullable = false)
	private Byte status;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private ArtistEntity owner;

	@Builder.Default
	@OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CustomProductEntity> customProducts = new LinkedHashSet<>();

	@Column(name = "provider_id", nullable = false, length = 13)
	private String providerId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", length = 1000)
	private String description;

	@Builder.Default
	@OneToMany(fetch = FetchType.LAZY,
		cascade = CascadeType.ALL,
		orphanRemoval = true)
	@JoinColumn(name = "campaign_id")
	@OrderBy("id.eventTime desc")
	private Set<CampaignHistoryEntity> campaignHistories = new LinkedHashSet<>();

}