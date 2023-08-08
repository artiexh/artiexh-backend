package com.artiexh.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.hypersistence.utils.hibernate.id.Tsid;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "base_model")
@EntityListeners(AuditingEntityListener.class)
public class BaseModelEntity extends BaseEntity{
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "model_file_url")
	private String modelFileUrl;

	@Type(JsonType.class)
	@Column(name = "sizes", columnDefinition = "json")
	private List<Size> sizes;

	@Column(name = "description", nullable = false)
	private String description;

	@OneToMany(mappedBy = "baseModel")
	private Set<ProvidedModelEntity> providedModels;
}
