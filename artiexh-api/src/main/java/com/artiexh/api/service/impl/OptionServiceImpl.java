package com.artiexh.api.service.impl;

import com.artiexh.api.service.OptionService;
import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.artiexh.data.jpa.entity.ProductOptionTemplateEntity;
import com.artiexh.data.jpa.repository.OptionTemplateRepository;
import com.artiexh.data.jpa.repository.ProductOptionRepository;
import com.artiexh.model.domain.ProductOption;
import com.artiexh.model.mapper.ProductOptionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
	private final ProductOptionRepository productOptionRepository;
	private final OptionTemplateRepository optionTemplateRepository;
	private final ProductOptionMapper productOptionMapper;

	@Override
	@Transactional(readOnly = true)
	public Page<ProductOption> getAll(Specification<ProductOptionEntity> specification, Pageable pageable) {
		Page<ProductOptionEntity> options = productOptionRepository.findAll(specification, pageable);
		return options.map(productOptionMapper::entityToDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductOption> getAllTemplate(Specification<ProductOptionTemplateEntity> specification, Pageable pageable) {
		Page<ProductOptionTemplateEntity> options = optionTemplateRepository.findAll(pageable);
		return options.map(productOptionMapper::entityToDomain);
	}
}
