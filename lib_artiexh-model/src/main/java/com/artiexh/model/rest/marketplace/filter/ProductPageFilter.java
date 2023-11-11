package com.artiexh.model.rest.marketplace.filter;

import com.artiexh.model.domain.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
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
	private Integer categoryId;
	@JsonIgnore
	private Long artistId;
	@JsonIgnore
	private String artistUsername;
	private Set<ProductStatus> statuses;

	public Query getQuery() {
		var boolQuery = new BoolQueryBuilder();

		if (artistId != null) {
			boolQuery.must(new TermQueryBuilder("owner.id", artistId));
		}

		if (artistUsername != null) {
			boolQuery.must(new TermQueryBuilder("owner.username", artistUsername));
		}

		if (statuses == null) {
			statuses = new LinkedHashSet<>();
		}
		statuses.add(ProductStatus.AVAILABLE);
		for (ProductStatus status : statuses) {
			boolQuery.should(new TermQueryBuilder("status", status.getValue()));
		}
		boolQuery.minimumShouldMatch(1);

		if (minPrice != null) {
			boolQuery.must(new RangeQueryBuilder("price.amount").gte(minPrice));
		}
		if (maxPrice != null) {
			boolQuery.must(new RangeQueryBuilder("price.amount").lte(maxPrice));
		}
		if (averageRate != null) {
			boolQuery.must(new TermQueryBuilder("averageRate", averageRate));
		}
		if (categoryId != null) {
			boolQuery.must(new TermQueryBuilder("category.id", categoryId));
		}

		var queryBuilder = new NativeSearchQueryBuilder().withFilter(boolQuery);

		if (StringUtils.hasText(keyword)) {
			queryBuilder.withQuery(new MultiMatchQueryBuilder(keyword, "name").fuzziness(Fuzziness.AUTO));
		}

		return queryBuilder.build();
	}
}
