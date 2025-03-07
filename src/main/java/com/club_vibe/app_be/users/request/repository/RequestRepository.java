package com.club_vibe.app_be.users.request.repository;

import com.club_vibe.app_be.users.request.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
}
