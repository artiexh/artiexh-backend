package com.artiexh.data.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByGoogleId(String googleId);

    Optional<UserEntity> findByFacebookId(String facebookId);

    Optional<UserEntity> findByTwitterId(String twitterId);

}
