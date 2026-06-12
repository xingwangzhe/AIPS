package com.needhelp.aips.controller;

import com.needhelp.aips.common.dto.ApiResponse;
import com.needhelp.aips.dto.consult.*;
import com.needhelp.aips.service.consult.ConsultService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 智能咨询接口。
 * POST /api/v1/consult/session
 * POST /api/v1/consult/session/{sessionId}/message
 * POST /api/v1/consult/session/{sessionId}/transfer
 */
@RestController
@RequestMapping("/api/v1/consult")
public class ConsultController {

    private final ConsultService consultService;

    public ConsultController(ConsultService consultService) {
        this.consultService = consultService;
    }

    @PostMapping("/session")
    public ApiResponse<SessionResponse> createSession(@RequestBody(required = false) SessionCreateRequest request,
                                                       Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        String title = request != null ? request.getTitle() : null;
        SessionResponse resp = consultService.createSession(userId, title);
        return ApiResponse.success(resp);
    }

    @PostMapping("/session/{sessionId}/message")
    public ApiResponse<AiReplyResponse> sendMessage(@PathVariable Long sessionId,
                                                     @RequestBody MessageRequest request,
                                                     Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        AiReplyResponse resp = consultService.sendMessage(userId, sessionId, request.getContent(), request.getMsgType());
        return ApiResponse.success(resp);
    }

    @PostMapping("/session/{sessionId}/transfer")
    public ApiResponse<TransferResponse> transfer(@PathVariable Long sessionId,
                                                   @RequestBody(required = false) TransferRequest request,
                                                   Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        String reason = request != null ? request.getReason() : null;
        TransferResponse resp = consultService.transferToHuman(userId, sessionId, reason);
        return ApiResponse.success(resp);
    }
}
