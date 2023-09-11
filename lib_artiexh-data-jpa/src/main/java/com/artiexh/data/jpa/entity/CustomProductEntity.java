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

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "custom_product")
public class CustomProductEntity {
	@Id
	@Tsid
	private Long id;

	@ManyToOne(optional = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "collection_id")
	private CollectionEntity collections;
}
