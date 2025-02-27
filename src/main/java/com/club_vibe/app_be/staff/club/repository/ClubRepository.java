package com.club_vibe.app_be.staff.club.repository;

import com.club_vibe.app_be.staff.club.entity.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, Long> {
}