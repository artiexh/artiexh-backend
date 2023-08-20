package com.artiexh.model.rest.product.request;

import com.artiexh.model.domain.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.*;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

	public Query getQuery() {
		var boolQuery = new BoolQueryBuilder().should(new TermsQueryBuilder("status", List.of(ProductStatus.PRE_ORDER.getValue(), ProductStatus.AVAILABLE.getValue()))).minimumShouldMatch(1);

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
		if (provinceId != null) {
			boolQuery.must(new TermQueryBuilder("owner.province.id", provinceId));
		}

		var queryBuilder = new NativeSearchQueryBuilder().withFilter(boolQuery);

		if (StringUtils.hasText(keyword)) {
			queryBuilder.withQuery(new MultiMatchQueryBuilder(keyword, "name", "owner.username", "owner.displayName").fuzziness(Fuzziness.AUTO));
		}

		return queryBuilder.build();
	}
}
