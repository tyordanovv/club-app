package com.club_vibe.app_be.staff.artist.repository;

import com.club_vibe.app_be.staff.artist.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
}
