package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProductAttachEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductAttachRepository extends JpaRepository<ProductAttachEntity, Long> {

	@Query("select a.url from ProductEntity p inner join p.attaches a where p.id = :productId and a.type = 1")
	Optional<String> findThumbnailByProductId(long productId);

}
