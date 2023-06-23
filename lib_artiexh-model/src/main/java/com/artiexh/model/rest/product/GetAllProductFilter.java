package com.artiexh.model.rest.product;

import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.domain.MerchStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.query.Criteria;
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
		return Specification.where(getFilterQuery()).and(getAvailableMerch());
	}

	private Specification<MerchEntity> getFilterQuery() {
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
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}

	private Specification<MerchEntity> getAvailableMerch() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("status"), MerchStatus.PRE_ORDER.getByteValue()));
			predicates.add(builder.equal(root.get("status"), MerchStatus.AVAILABLE.getByteValue()));
			return builder.or(predicates.toArray(new Predicate[0]));
		};
	}

	public Criteria getEsCriteria() {
		return getAvailableESCriteria().and(getFilterESCriteria());
	}

	private Criteria getFilterESCriteria() {
		Criteria criteria = new Criteria();
		if (StringUtils.isNotBlank(keyword)) {
			criteria.and("name").matches(keyword);
		}
		if (minPrice != null) {
			criteria.and("price").greaterThanEqual(minPrice);
		}
		if (maxPrice != null) {
			criteria.and("price").lessThanEqual(maxPrice);
		}
		if (averageRate != null) {
			criteria.and("averageRate").is(averageRate);
		}
		if (categoryId != null) {
			criteria.and("category.id").is(categoryId);
		}
		if (provinceId != null) {
			criteria.and("owner.province.id").is(provinceId);
		}
		return criteria;
	}

	private Criteria getAvailableESCriteria() {
		return new Criteria("status")
			.in(MerchStatus.PRE_ORDER.getByteValue(), MerchStatus.AVAILABLE.getByteValue());
	}
}
