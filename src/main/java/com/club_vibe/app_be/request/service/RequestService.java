package com.club_vibe.app_be.request.service;

import com.club_vibe.app_be.events.dto.RequestStatus;
import com.club_vibe.app_be.request.dto.reqest.InitializeRequest;

public interface RequestService {

    /**
     *
     * @param request
     * @return
     */
    Long initializeRequest(InitializeRequest request);

    /**
     *
     * @param requestId
     * @param requestStatus
     */
    void updateRequestStatus(Long requestId, RequestStatus requestStatus);
}
