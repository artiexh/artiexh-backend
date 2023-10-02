package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
	private Long id;
	private String name;
	private ProductVariant variant;
	private Artist artist;
	private Set<ImageSet> imageSet;
	private String combinationCode;
	private Boolean isLock;
}
