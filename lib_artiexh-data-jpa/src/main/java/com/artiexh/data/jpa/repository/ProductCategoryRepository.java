package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {
	Set<ProductCategoryEntity> findAllByNameIn(List<String> names);
}
