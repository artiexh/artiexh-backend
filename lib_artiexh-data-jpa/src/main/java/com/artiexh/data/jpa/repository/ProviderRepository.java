package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, String> {
}
