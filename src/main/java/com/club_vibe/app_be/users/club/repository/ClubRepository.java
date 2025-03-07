package com.club_vibe.app_be.users.club.repository;

import com.club_vibe.app_be.users.club.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, Long> {
    Optional<ClubEntity> findByEmail(String email);
}