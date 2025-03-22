package com.club_vibe.app_be.request.mapper;

import com.club_vibe.app_be.request.dto.RequestDto;
import com.club_vibe.app_be.request.entity.RequestEntity;
import org.springframework.stereotype.Service;

@Service
public class RequestMapper {
    public RequestDto mapRequestToDTO(RequestEntity request) {
        return new RequestDto(
                request.getId(),
                request.getType(),
                request.getTitle(),
                request.getMessage(),
                request.getGuestEmail()
        );
    }
}
