package com.artiexh.model.rest.productbase;

import com.artiexh.data.jpa.entity.embededmodel.Size;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseDetail extends ProductBaseInfo{

	private String description;
}
