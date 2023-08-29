package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBase {
	private Long id;

	private String name;

	private String type;

	private String productFileUrl;

	private List<Size> sizes;

	private String description;

	private Model3DCode model3DCode;
}
