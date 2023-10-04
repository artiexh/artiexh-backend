package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ProviderEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, String>, JpaSpecificationExecutor<ProviderEntity> {
	@Query(nativeQuery = true, value = """
		select count(*) from product_base_provider_mapping
		where product_base_id = :productBaseId and business_code in :businessCodes
		""")
	int countProvider(@NotNull @Param("productBaseId") Long productBaseId, @NotNull @Param("businessCodes") List<String> businessCodes);

	@Query("""
		select provider
		from ProviderEntity provider
		left join ProductVariantProviderEntity config on provider.businessCode = config.id.businessCode
		where config.id.productVariantId in :variantIds
		group by provider
		having count(config.id.productVariantId) = :numOfVariant""")
	Set<ProviderEntity> findAllByProductVariantIds(@Param("variantIds") Set<Long> variantIds, @Param("numOfVariant") Integer numOfVariant);
}
