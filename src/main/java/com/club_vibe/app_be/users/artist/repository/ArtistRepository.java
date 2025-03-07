package com.club_vibe.app_be.users.artist.repository;

import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    Optional<ArtistEntity> findByEmail(String email);
}
