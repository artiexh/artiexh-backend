package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.data.jpa.entity.MerchTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MerchTagRepository extends JpaRepository<MerchTagEntity, Long> {
	Set<MerchTagEntity> findAllByNameIn(List<String> names);
}
