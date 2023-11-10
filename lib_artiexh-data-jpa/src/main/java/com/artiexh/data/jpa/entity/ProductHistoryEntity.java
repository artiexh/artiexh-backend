package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_history")
@EntityListeners(AuditingEntityListener.class)
public class ProductHistoryEntity extends BaseAuditEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "source_category")
	private Byte sourceCategory;

	@Column(name = "action", nullable = false)
	private Byte action;

	@Column(name = "source_id")
	private Long sourceId;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "product_history_id")
	private Set<ProductHistoryDetailEntity> productHistoryDetails = new HashSet<>();
}
