package com.artiexh.model.rest.order.request;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.OrderStatus;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageFilter {

	private OrderStatus status;
	private LocalDateTime from;
	private LocalDateTime to;

	public Specification<OrderEntity> getSpecificationForArtist(Long shopId) {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("shop").get("id"), shopId));
			if (status != null) {
				predicates.add(builder.equal(root.get("status"), status.getByteValue()));
			}
			if (from != null) {
				predicates.add(builder.greaterThanOrEqualTo(root.get("createdDate"), from));
			}
			if (to != null) {
				predicates.add(builder.lessThanOrEqualTo(root.get("createdDate"), from));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<OrderEntity> getSpecificationForUser(Long userId) {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("user").get("id"), userId));
			if (status != null) {
				predicates.add(builder.equal(root.get("status"), status.getByteValue()));
			}
			if (from != null) {
				predicates.add(builder.greaterThanOrEqualTo(root.get("createdDate"), from));
			}
			if (to != null) {
				predicates.add(builder.lessThanOrEqualTo(root.get("createdDate"), from));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
