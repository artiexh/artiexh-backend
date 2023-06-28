package com.artiexh.model.rest.product.request;

import co.elastic.clients.json.JsonData;
import com.artiexh.model.domain.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.math.BigDecimal;


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

	public Query getQuery() {
		return NativeQuery.builder()
			.withFilter(filter -> {
				filter.term(term -> term.field("status")
					.value(ProductStatus.PRE_ORDER.getByteValue())
					.value(ProductStatus.AVAILABLE.getByteValue())
				);
				if (minPrice != null) {
					filter.range(range -> range.field("price.amount").gte(JsonData.of(minPrice)));
				}
				if (maxPrice != null) {
					filter.range(range -> range.field("price.amount").lte(JsonData.of(maxPrice)));
				}
				if (averageRate != null) {
					filter.term(term -> term.field("averageRate").value(averageRate));
				}
				if (categoryId != null) {
					filter.term(term -> term.field("category.id").value(categoryId));
				}
				if (provinceId != null) {
					filter.term(term -> term.field("owner.province.id").value(provinceId));
				}
				return filter;
			})
			.withQuery(query -> query
				.multiMatch(multiMatch -> multiMatch
					.query(keyword)
					.fields("name", "owner.username", "owner.displayName")
					.fuzziness("AUTO"))
			)
			.build();
	}
}
