package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductAttachEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProductAttachRepository extends JpaRepository<ProductAttachEntity, Long> {
	Set<ProductAttachEntity> findAllByIdIn(Set<Long> ids);

}
