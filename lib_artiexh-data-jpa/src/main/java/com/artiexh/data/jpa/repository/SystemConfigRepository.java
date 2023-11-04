package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfigEntity, String> {
	Stream<SystemConfigEntity> streamAll();
}
