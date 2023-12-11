package com.artiexh.data.jpa.entity.embededmodel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferenceData {
	private Long id;
	private ReferenceEntity referenceEntity;
}
