package com.needhelp.aips.infrastructure.file;

import org.springframework.stereotype.Service;

/**
 * 文件中心服务（空壳）。
 * TODO: 对接 MinIO / OSS 对象存储。
 */
@Service
public class FileService {

    /**
     * 上传文件。
     * TODO: 实现文件上传到对象存储，返回文件 URL。
     */
    public String upload(byte[] fileData, String fileName, String contentType) {
        // TODO: 实际对接 MinIO / OSS
        return "/uploads/" + System.currentTimeMillis() + "_" + fileName;
    }
}
