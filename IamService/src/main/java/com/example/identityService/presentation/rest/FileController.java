package com.example.identityService.presentation.rest;

import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.ApiResponse;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.service.FileService;
import com.devdeli.common.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final FileService fileService;

//    public
    @PostMapping("/public/files/upload")
    public ApiResponse<List<FileResponse>> uploadPublicFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        List<FileResponse> result = fileService.uploadPublicFiles(files, getCurrentOwner());
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

        return fileService.downloadPublicFile(fileId, width, height, ratio);
    }

    @GetMapping("/public/files/{fileId}/info")
    public ApiResponse<FileResponse> getFileInfo(@PathVariable String fileId)  {
        return ApiResponse.<FileResponse>builder()
                .code(200)
                .result(fileService.getPublicFileInfo(fileId))
                .build();
    }

    @DeleteMapping("/public/files/{fileId}")
    @PreAuthorize("hasPermission(#fileId, 'files.delete')")
    public ApiResponse<Boolean> deleteFile(@PathVariable String fileId)  {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileService.deletePublicFile(fileId)))
                .build();
    }

//    private
    @PostMapping("/files/upload")
    @PreAuthorize("hasPermission(null, 'files.create')")
    public ApiResponse<List<FileResponse>> uploadPrivateFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        List<FileResponse> result = fileService.uploadPrivateFiles(files, getCurrentOwner());
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

        return fileService.downloadPrivateFile(fileId, width, height, ratio);
    }

    @GetMapping("/files/{fileId}/info")
    @PreAuthorize("hasPermission(#fileId, 'files.read')")
    public ApiResponse<FileResponse> getPrivateFileInfo(@PathVariable String fileId)  {
        return ApiResponse.<FileResponse>builder()
                .code(200)
                .result(fileService.getPrivateFileInfo(fileId))
                .build();
    }

    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("hasPermission(#fileId, 'files.delete')")
    public ApiResponse<Boolean> deletePrivateFile(@PathVariable String fileId)  {
        return ApiResponse.<Boolean>builder()
                .code(200)
                .message(ApiResponse.setResponseMessage(fileService.deletePrivateFile(fileId)))
                .build();
    }

    @GetMapping("/files")
    @PreAuthorize("hasPermission(null, 'files.read')")
    public ApiResponse<PageResponse<FileResponse>> getFile(@ModelAttribute FilePageRequest request) {
        return ApiResponse.<PageResponse<FileResponse>>builder()
                .code(200)
                .result(fileService.getFiles(request))
                .build();
    }

    private String getCurrentOwner(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
