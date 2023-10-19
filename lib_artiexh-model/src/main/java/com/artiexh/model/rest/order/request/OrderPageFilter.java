package com.artiexh.model.rest.order.request;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.OrderStatus;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageFilter {
	private OrderStatus status;
	private Instant from;
	private Instant to;

	public Specification<OrderEntity> getSpecificationForArtist(Long shopId) {
		return getSpecification(root -> root.get("shop").get("id"), shopId);
	}

	public Specification<OrderEntity> getSpecificationForUser(Long userId) {
		return getSpecification(root -> root.get("orderGroup").get("user").get("id"), userId);
	}

	private Specification<OrderEntity> getSpecification(Function<Root<OrderEntity>, Path<Long>> identifier, Long id) {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(identifier.apply(root), id));
			if (status != null) {
				predicates.add(builder.equal(root.get("status"), status.getByteValue()));
			}
			if (from != null) {
				predicates.add(builder.greaterThanOrEqualTo(root.get("createdDate"), from));
			}
			if (to != null) {
				predicates.add(builder.lessThanOrEqualTo(root.get("createdDate"), to));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}


}
