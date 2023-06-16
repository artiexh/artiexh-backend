package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.MerchAttachEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface MerchAttachRepository extends JpaRepository<MerchAttachEntity, Long> {
	Set<MerchAttachEntity> findAllByIdIn(Set<Long> ids);

}
