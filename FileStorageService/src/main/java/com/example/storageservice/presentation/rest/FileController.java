package com.example.storageservice.presentation.rest;

import com.devdeli.common.dto.request.FileActionPageRequest;
import com.devdeli.common.dto.response.PageResponse;
import com.example.storageservice.application.DTO.ApiResponse;
import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.FileResponse;
import com.example.storageservice.domain.FileAction;
import com.example.storageservice.application.service.FileActionService;
import com.example.storageservice.application.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;
    private final FileActionService fileActionService;

//    public
    @PostMapping("/public/files/upload")
    public ApiResponse<List<FileResponse>> uploadPublicFiles(@RequestParam("files") List<MultipartFile> files,
                                     @RequestParam("ownerId") String ownerId
                                     ) throws IOException {
        List<FileResponse> result = fileStorageService.storeFiles(ownerId, true, files);
        return ApiResponse.<List<FileResponse>>builder()
                .code(200)
                .result(result)
                .message(ApiResponse.setResponseMessage(result!=null))
                .build();
    }

    @GetMapping("/public/files/{fileId}")
    public ResponseEntity<byte[]> getFilePublic(@PathVariable String fileId,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) throws IOException {
        return fileStorageService.loadFileAsResource(fileId, true, width, height, ratio);
    }

    @GetMapping("/public/files/{fileId}/info")
    public ApiResponse<FileResponse> getFileInfo(@PathVariable String fileId)  {
        return ApiResponse.<FileResponse>builder()
                .code(200)
                .result(fileStorageService.getFileInfo(fileId, true))
                .build();
    }

    @DeleteMapping("/public/files/{fileId}")
    @PreAuthorize("hasPermission(#fileId, 'files.delete')")
    public ApiResponse<Boolean> deleteFile(@PathVariable String fileId)  {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.deleteFile(fileId, true)))
                .build();
    }

//    private
    @PostMapping("/files/upload")
    @PreAuthorize("hasPermission(null, 'files.create')")
    public ApiResponse<List<FileResponse>> uploadPrivateFiles(@RequestParam("files") List<MultipartFile> files,
                                             @RequestParam("ownerId") String ownerId) throws IOException {
        List<FileResponse> result = fileStorageService.storeFiles(ownerId, false, files);
        return ApiResponse.<List<FileResponse>>builder()
                .code(200)
                .result(result)
                .message(ApiResponse.setResponseMessage(result!=null))
                .build();
    }

    @GetMapping("/files/{fileId}")
    @PreAuthorize("hasPermission(#fileId, 'files.read')")
    public ResponseEntity<byte[]> getFilePrivate(@PathVariable String fileId,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) throws IOException {
        return fileStorageService.loadFileAsResource(fileId, false, width, height, ratio);
    }

    @GetMapping("/files/{fileId}/info")
    @PreAuthorize("hasPermission(#fileId, 'files.read')")
    public ApiResponse<FileResponse> getPrivateFileInfo(@PathVariable String fileId)  {
        return ApiResponse.<FileResponse>builder()
                .code(200)
                .result(fileStorageService.getFileInfo(fileId, false))
                .build();
    }

    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasPermission(#fileId, 'files.delete')")
    public ApiResponse<Boolean> deletePrivateFile(@PathVariable String fileId)  {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileStorageService.deleteFile(fileId, false)))
                .build();
    }

    @GetMapping("/files")
    @PreAuthorize("hasPermission(null, 'files.read')")
    public ApiResponse<PageResponse<FileResponse>> getFile(@ModelAttribute FilePageRequest request) {
        return ApiResponse.<PageResponse<FileResponse>>builder()
                .code(200)
                .result(fileStorageService.getFiles(request))
                .build();
    }

    @GetMapping("/files/histories")
    @PreAuthorize("hasPermission(null, 'files.read')")
    public ApiResponse<PageResponse<FileAction>> getFileActionHistory(@ModelAttribute FileActionPageRequest request) {
        return ApiResponse.<PageResponse<FileAction>>builder()
                .code(200)
                .result(fileActionService.getFiles(request))
                .build();
    }
}
