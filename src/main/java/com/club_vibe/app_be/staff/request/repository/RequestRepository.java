package com.club_vibe.app_be.staff.request.repository;

import com.club_vibe.app_be.staff.request.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
}
