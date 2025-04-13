package com.club_vibe.app_be.events.repository;

import com.club_vibe.app_be.events.entity.EventConditionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventConditionsRepository extends JpaRepository<EventConditionsEntity, Long> {
}
