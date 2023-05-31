package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
}
