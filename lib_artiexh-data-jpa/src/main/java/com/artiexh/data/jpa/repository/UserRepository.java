package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByGoogleId(Long googleId);

    Optional<UserEntity> findByFacebookId(Long facebookId);

    Optional<UserEntity> findByTwitterId(Long twitterId);

}
