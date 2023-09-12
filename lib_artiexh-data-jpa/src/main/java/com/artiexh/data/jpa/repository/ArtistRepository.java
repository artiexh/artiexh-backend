package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long>, JpaSpecificationExecutor<ArtistEntity> {

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query(value = "INSERT INTO artist(id) VALUES(:id)", nativeQuery = true)
	void createArtistByExistedUserId(@Param("id") Long id);

}
