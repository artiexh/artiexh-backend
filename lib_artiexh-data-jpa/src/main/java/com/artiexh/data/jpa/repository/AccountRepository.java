package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

	Optional<AccountEntity> findByUsername(String username);

	@Query("select count(account.id) from AccountEntity account where account.id in :ids")
	int countByIdsIn(@Param("ids") Long[] ids);
}
