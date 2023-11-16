package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campaign_sale")
@EntityListeners(AuditingEntityListener.class)
public class CampaignSaleEntity extends BaseAuditEntity {
	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "public_date", nullable = false)
	private Instant publicDate;

	@Column(name = "from", nullable = false)
	private Instant from;

	@Column(name = "to", nullable = false)
	private Instant to;

	@Column(name = "created_by", nullable = false)
	private Long createdBy;

	@Builder.Default
	@OneToMany(mappedBy = "campaignSale", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProductEntity> products = new LinkedHashSet<>();

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private ArtistEntity owner;

	@Lob
	@Column(name = "content")
	private String content;

	@Size(max = 2048)
	@Column(name = "thumbnail_url", length = 2048)
	private String thumbnailUrl;

	@NotNull
	@Column(name = "type", nullable = false)
	private Byte type;

	@Column(name = "campaign_request_id")
	private Long campaignRequestId;

}