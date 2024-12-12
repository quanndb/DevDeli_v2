package com.example.identityService.repository.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(
        url = "http://localhost:2819/api/v1.0.0",
        name = "storageClient"
        )
public interface StorageClient {
    @PostMapping("/files/upload")
    Object upPrivateFile(@RequestParam("files") List<MultipartFile> files,
                         @RequestParam("ownerId") String ownerId,
                         @RequestHeader(name = "Authorization") String authHeader);
}
