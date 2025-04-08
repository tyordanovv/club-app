package com.club_vibe.app_be.request.service.impl;

import com.club_vibe.app_be.events.dto.RequestStatus;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.request.dto.reqest.InitializeRequest;
import com.club_vibe.app_be.request.entity.RequestEntity;
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

    @Override
    public Long initializeRequest(InitializeRequest createRequest) {
        RequestEntity request = RequestEntity.builder()
                .title(createRequest.title())
                .message(createRequest.message())
                .guestEmail(createRequest.userEmail())
                .event(entityManager.getReference(EventEntity.class, createRequest.eventId()))
                .type(createRequest.type())
                .status(RequestStatus.INITIALIZED)
                .build();
        return requestRepository.save(request).getId();
    }

    @Override
    public void updateRequestStatus(Long requestId, RequestStatus requestStatus) {
        int updatedRows = requestRepository.updateStatus(requestId, requestStatus);
        if (updatedRows == 0) {
            // TODO log and error
        }
    }
}
