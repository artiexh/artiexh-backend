package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "base_model")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BaseModelEntity extends BaseAuditEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "model_file_url", nullable = false)
	private String modelFileUrl;

	@Type(JsonType.class)
	@Column(name = "sizes", columnDefinition = "json", nullable = false)
	private List<Size> sizes;

	@Column(name = "description", nullable = false)
	private String description;

	@OneToMany(mappedBy = "baseModel")
	private Set<ProvidedModelEntity> providedModels;
}
