package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.InventoryItemEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, Long>, JpaSpecificationExecutor<InventoryItemEntity> {
	Optional<InventoryItemEntity> findInventoryItemEntityByIdAndArtistId(Long id, Long artistId);

	@Modifying
	@Query(value = "delete from InventoryItemEntity item where item.id = :id")
	void deleteById(@NotNull @Param("id") Long id);
}
