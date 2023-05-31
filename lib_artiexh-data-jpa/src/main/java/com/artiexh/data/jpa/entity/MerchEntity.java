package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "merch")
@Inheritance(strategy = InheritanceType.JOINED)
public class MerchEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "owner_id", nullable = false)
	private ArtistEntity owner;

	@Column(name = "status", nullable = false)
	private Byte status;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "price", nullable = false)
	private Double price;

	@Column(name = "description")
	private String description;

	@Column(name = "type", nullable = false)
	private Byte type;

	@Column(name = "remaining_quantity", columnDefinition = "INT UNSIGNED not null")
	private Long remainingQuantity;

	@Column(name = "publish_datetime")
	private LocalDateTime publishDatetime;

	@Column(name = "max_items_per_order", columnDefinition = "INT UNSIGNED")
	private Long maxItemsPerOrder;

	@Column(name = "delivery_type", nullable = false)
	private Byte deliveryType;

	@ManyToMany
	@JoinTable(name = "merch_category_mapping",
		joinColumns = @JoinColumn(name = "merch_id"),
		inverseJoinColumns = @JoinColumn(name = "category_id"))
	private Set<MerchCategoryEntity> categories;

	@ManyToMany
	@JoinTable(name = "merch_tag_mapping",
		joinColumns = @JoinColumn(name = "merch_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private Set<MerchTagEntity> tags;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "merch_id")
	private Set<MerchAttachEntity> attaches ;

}