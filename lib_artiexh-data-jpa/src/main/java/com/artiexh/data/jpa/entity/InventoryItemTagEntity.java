package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(InventoryItemTagId.class)
@Table(name = "inventory_item_tag")
public class InventoryItemTagEntity {

	@Id
	@Column(name = "inventory_item_id", nullable = false)
	private Long inventoryItemId;

	@Id
	@Column(name = "name", nullable = false)
	private String name;

}