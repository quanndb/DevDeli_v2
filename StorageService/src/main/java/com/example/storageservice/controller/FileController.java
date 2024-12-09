package com.example.storageservice.controller;

import com.example.storageservice.DTO.ApiResponse;
import com.example.storageservice.DTO.request.FilePageRequest;
import com.example.storageservice.DTO.response.FileResponse;
import com.example.storageservice.DTO.response.PageResponse;
import com.example.storageservice.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ApiResponse<?> uploadFile(@RequestParam("file") List<MultipartFile> files,
                                     @RequestParam("ownerId") String ownerId,
                                     @RequestParam("isPublic") boolean isPublic) throws IOException {
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.storeFiles(ownerId, isPublic, files)))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<FileResponse>> getFile(@ModelAttribute FilePageRequest request) {
        return ApiResponse.<PageResponse<FileResponse>>builder()
                .code(200)
                .result(fileStorageService.getFiles(request))
                .build();
    }

    @GetMapping("/sharing/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName, HttpServletRequest request,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) throws IOException {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader == null ? null : authHeader.substring(7);
        Resource resource = fileStorageService.loadFileAsResource(fileName, token, width, height, ratio);

        String contentType;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/sharing/{fileName}/info")
    public ApiResponse<FileResponse> getFileInfo(@PathVariable String fileName, HttpServletRequest request)  {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader == null ? null : authHeader.substring(7);

        return ApiResponse.<FileResponse>builder()
                .code(200)
                .result(fileStorageService.getFileInfo(fileName, token))
                .build();
    }

    @DeleteMapping("/{fileName}")
    public ApiResponse<Boolean> getFileInfo(@PathVariable String fileName)  {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.deleteFile(fileName)))
                .build();
    }
}
