package com.artiexh.api.service.impl;

import com.artiexh.api.service.OptionService;
import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.artiexh.data.jpa.entity.ProductOptionTemplateEntity;
import com.artiexh.data.jpa.repository.OptionTemplateRepository;
import com.artiexh.data.jpa.repository.ProductOptionRepository;
import com.artiexh.data.opensearch.model.OptionTemplateDocument;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.ProductOption;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.mapper.ProductOptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
	private final ProductOptionRepository productOptionRepository;
	private final OptionTemplateRepository optionTemplateRepository;
	private final ProductOptionMapper productOptionMapper;
	private final ElasticsearchOperations openSearchTemplate;

	@Override
	@Transactional(readOnly = true)
	public Page<ProductOption> getAll(Specification<ProductOptionEntity> specification, Pageable pageable) {
		Page<ProductOptionEntity> options = productOptionRepository.findAll(specification, pageable);
		return options.map(productOptionMapper::entityToDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductOption> getAllTemplate(Pageable pageable) {
		Query query = new CriteriaQuery(new Criteria());
		query.setPageable(pageable);
		SearchHits<OptionTemplateDocument> hits = openSearchTemplate.search(query, OptionTemplateDocument.class);
		SearchPage<OptionTemplateDocument> hitPage = SearchHitSupport.searchPageFor(hits, pageable);
		return hitPage.map(searchHit -> productOptionMapper.documentToDomain(searchHit.getContent()));
	}
}
