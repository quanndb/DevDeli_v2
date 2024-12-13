package com.example.storageservice.controller;

import com.example.storageservice.DTO.ApiResponse;
import com.example.storageservice.DTO.request.FilePageRequest;
import com.example.storageservice.DTO.response.FileResponse;
import com.example.storageservice.DTO.response.PageResponse;
import com.example.storageservice.service.FileStorageService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;

//    public
    @PostMapping("/public/files/upload")
    public ApiResponse<?> uploadPublicFiles(@RequestParam("files") List<MultipartFile> files,
                                     @RequestParam("ownerId") String ownerId
                                     ) throws IOException {
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.storeFiles(ownerId, true, files)))
                .build();
    }

    @GetMapping("/public/files/{fileName}/download")
    public ResponseEntity<Resource> getFilePublic(@PathVariable String fileName,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) throws IOException {

        Resource resource = fileStorageService.loadFileAsResource(fileName, true, width, height, ratio);

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

    @GetMapping("/public/files/{fileName}/info")
    public ApiResponse<FileResponse> getFileInfo(@PathVariable String fileName)  {
        return ApiResponse.<FileResponse>builder()
                .code(200)
                .result(fileStorageService.getFileInfo(fileName, true))
                .build();
    }

    @DeleteMapping("/public/files/{fileName}")
    public ApiResponse<Boolean> deleteFile(@PathVariable String fileName)  {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.deleteFile(fileName, true)))
                .build();
    }

//    private
    @PostMapping("/files/upload")
    public ApiResponse<?> uploadPrivateFiles(@RequestParam("files") List<MultipartFile> files,
                                             @RequestParam("ownerId") String ownerId) throws IOException {
        return ApiResponse.builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.storeFiles(ownerId, false, files)))
                .build();
    }

    @GetMapping("/files/{fileName}/download")
    public ResponseEntity<Resource> getFilePrivate(@PathVariable String fileName,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) throws IOException {

        Resource resource = fileStorageService.loadFileAsResource(fileName, false, width, height, ratio);

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

    @GetMapping("/files/{fileName}/info")
    public ApiResponse<FileResponse> getPrivateFileInfo(@PathVariable String fileName)  {
        return ApiResponse.<FileResponse>builder()
                .code(200)
                .result(fileStorageService.getFileInfo(fileName, false))
                .build();
    }

    @DeleteMapping("/files/{fileName}")
    public ApiResponse<Boolean> deletePrivateFile(@PathVariable String fileName)  {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.deleteFile(fileName, false)))
                .build();
    }

    @GetMapping("/files")
    public ApiResponse<PageResponse<FileResponse>> getFile(@ModelAttribute FilePageRequest request) {
        return ApiResponse.<PageResponse<FileResponse>>builder()
                .code(200)
                .result(fileStorageService.getFiles(request))
                .build();
    }
}
