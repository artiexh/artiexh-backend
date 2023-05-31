package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.MerchCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MerchCategoryRepository extends JpaRepository<MerchCategoryEntity, Long> {
	Set<MerchCategoryEntity> findAllByNameIn(List<String> names);
}
