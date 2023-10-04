package com.artiexh.data.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemTagId implements Serializable {

	private Long inventoryItemId;

	private String name;

}