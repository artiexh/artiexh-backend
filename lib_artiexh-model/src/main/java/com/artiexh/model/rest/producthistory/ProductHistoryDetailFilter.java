package com.artiexh.model.rest.producthistory;

import com.artiexh.data.jpa.entity.ProductHistoryDetailEntity;
import com.artiexh.data.jpa.entity.ProductHistoryEntity;
import com.artiexh.model.domain.ProductHistoryAction;
import com.artiexh.model.domain.ProductHistoryDetail;
import com.artiexh.model.domain.SourceCategory;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductHistoryDetailFilter {
	private String productCode;
	public Specification<ProductHistoryDetailEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(productCode)) {
				predicates.add(builder.equal(root.get("productInventory").get("productCode"), productCode));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
