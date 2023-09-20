package com.artiexh.data.jpa.entity.embededmodel;

import io.hypersistence.utils.hibernate.id.Tsid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImageCombination {
	private String name;
	private String code = UUID.randomUUID().toString();
	private List<ImageConfig> images;
}
