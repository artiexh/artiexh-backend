package com.artiexh.model.rest.artist.filter;

import co.elastic.clients.json.JsonData;
import com.artiexh.model.domain.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageFilter {
	private String keyword;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private Float averageRate;
	private Integer provinceId;
	private Integer categoryId;
	@JsonIgnore
	private Long artistId;
	private Set<ProductStatus> statuses;

	public Query getQuery() {
		var queryBuilder = NativeQuery.builder()
			.withFilter(filter -> filter.bool(bool -> {
				bool.must(must -> must.term(term -> term.field("owner.id").value(artistId)));

				if (statuses == null) {
					statuses = new LinkedHashSet<>();
				}
				statuses.add(ProductStatus.AVAILABLE);
				statuses.add(ProductStatus.PRE_ORDER);
				for (ProductStatus status : statuses) {
					bool.should(should -> should.term(term -> term.field("status").value(status.getValue())));
				}
				bool.minimumShouldMatch("1");

				if (minPrice != null) {
					bool.must(must -> must.range(range -> range.field("price.amount").gte(JsonData.of(minPrice))));
				}
				if (maxPrice != null) {
					bool.must(must -> must.range(range -> range.field("price.amount").lte(JsonData.of(maxPrice))));
				}
				if (averageRate != null) {
					bool.must(must -> must.term(term -> term.field("averageRate").value(averageRate)));
				}
				if (categoryId != null) {
					bool.must(must -> must.term(term -> term.field("category.id").value(categoryId)));
				}
				if (provinceId != null) {
					bool.must(must -> must.term(term -> term.field("owner.province.id").value(provinceId)));
				}
				return bool;
			}));

		if (StringUtils.hasText(keyword)) {
			queryBuilder.withQuery(query -> query
				.multiMatch(multiMatch -> multiMatch
					.query(keyword)
					.fields("name")
					.fuzziness("AUTO"))
			);
		}

		return queryBuilder.build();
	}
}
