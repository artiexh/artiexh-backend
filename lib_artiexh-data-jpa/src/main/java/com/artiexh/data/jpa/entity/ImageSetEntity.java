package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "image_set")
public class ImageSetEntity {
	@Id
	@Tsid
	private Long id;
	@Column(name = "position_code")
	private String positionCode;

	@OneToOne()
	@JoinColumn(name = "manufacturing_image_id", referencedColumnName = "id")
	private MediaEntity manufacturingImage;

	@OneToOne()
	@JoinColumn(name = "mockup_image_id", referencedColumnName = "id")
	private MediaEntity mockupImage;

//	@Column(name = "inventory_item_id")
//	private Long inventoryItemId;
}
