package com.artiexh.model.rest.productbase;

import com.artiexh.data.jpa.entity.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseDetail extends ProductBaseInfo{
	@NotEmpty
	private List<Size> sizes;

	private String description;
}
