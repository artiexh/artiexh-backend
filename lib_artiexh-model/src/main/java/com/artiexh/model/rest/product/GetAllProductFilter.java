package com.artiexh.model.rest.product;

import com.artiexh.data.jpa.entity.MerchEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllProductFilter {
	private String keyword;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private Float averageRate;
	private Integer provinceId;
	private Integer categoryId;

	public Specification<MerchEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.isNotBlank(keyword)) {
				predicates.add(builder.like(root.get("name"), "%" + keyword + "%"));
			}
			if (minPrice != null) {
				predicates.add(builder.ge(root.get("price"), minPrice));
			}
			if (maxPrice != null) {
				predicates.add(builder.le(root.get("price"), maxPrice));
			}
			if (averageRate != null) {
				predicates.add(builder.equal(root.get("averageRate"), averageRate));
			}
			if (categoryId != null) {
				predicates.add(builder.equal(root.get("category").get("id"), categoryId));
			}
			if (provinceId != null) {
				predicates.add(builder.equal(root.join("owner").get("province").get("id"), provinceId));
			}
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}
}
