package com.club_vibe.app_be.request.service.impl;

import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.request.dto.RequestDto;
import com.club_vibe.app_be.request.dto.reqest.InitializeRequest;
import com.club_vibe.app_be.request.entity.RequestEntity;
import com.club_vibe.app_be.request.mapper.RequestMapper;
import com.club_vibe.app_be.request.repository.RequestRepository;
import com.club_vibe.app_be.request.service.RequestService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EntityManager entityManager;
    private final RequestMapper requestMapper;

    @Override
    public Long initializeRequest(InitializeRequest request) {
        return saveRequestAndMapToDTO(request).requestId();
    }

    private RequestDto saveRequestAndMapToDTO(InitializeRequest createRequest) {
        RequestEntity request = new RequestEntity();
        request.setTitle(createRequest.title());
        request.setMessage(createRequest.message());
        request.setGuestEmail(createRequest.userEmail());
        request.setEvent(entityManager.getReference(EventEntity.class, createRequest.eventId()));
        request.setType(createRequest.type());
        return requestMapper.mapRequestToDTO(requestRepository.save(request));
    }
}
