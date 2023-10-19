package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.MediaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
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
	private Long campaignLock;
	private String description;
	private Set<String> tags;
	private Media thumbnail;
	private Instant createdDate;
	private Instant modifiedDate;
}
