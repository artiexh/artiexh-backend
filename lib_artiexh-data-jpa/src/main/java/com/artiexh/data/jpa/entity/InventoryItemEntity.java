package com.artiexh.data.jpa.entity;

import com.fasterxml.jackson.annotation.JacksonInject;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "inventory_item")
public class InventoryItemEntity {
	@Id
	@Tsid
	private Long id;

	@OneToMany(orphanRemoval = true,
		cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "inventory_item_id")
	private Set<ImageSetEntity> imageSet;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "artist_id", nullable = false)
	private ArtistEntity artist;

	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REMOVE})
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "variant_id", nullable = false)
	private ProductVariantEntity variant;

	private String combinationCode;
}
