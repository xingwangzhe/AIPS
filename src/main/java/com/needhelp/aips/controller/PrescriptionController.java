package com.needhelp.aips.controller;

import com.needhelp.aips.common.dto.ApiResponse;
import com.needhelp.aips.dto.prescription.PrescriptionStatusResponse;
import com.needhelp.aips.dto.prescription.PrescriptionUploadResponse;
import com.needhelp.aips.service.prescription.PrescriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 处方接口。
 * POST /api/v1/prescription/upload
 * GET  /api/v1/prescription/{prescriptionId}
 */
@RestController
@RequestMapping("/api/v1/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/upload")
    public ApiResponse<PrescriptionUploadResponse> upload(@RequestParam("file") MultipartFile file,
                                                           Authentication auth) {
        Long userId = Long.parseLong(auth.getPrincipal().toString());
        // 模拟图片 URL（实际应为文件中心上传后返回的 URL）
        String imageUrl = "/uploads/prescriptions/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        PrescriptionUploadResponse resp = prescriptionService.upload(userId, imageUrl);
        return ApiResponse.success(resp);
    }

    @GetMapping("/{prescriptionId}")
    public ApiResponse<PrescriptionStatusResponse> getStatus(@PathVariable Long prescriptionId) {
        PrescriptionStatusResponse resp = prescriptionService.getStatus(prescriptionId);
        return ApiResponse.success(resp);
    }
}
