package com.artiexh.model.rest.productbase;

import com.artiexh.data.jpa.entity.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseDetail extends ProductBaseInfo{
	private List<Size> sizes;

	private String description;
}
