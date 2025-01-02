package com.devdeli.common.client.storage;

import com.devdeli.common.config.FeignClientConfiguration;
import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.ApiResponse;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.dto.response.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@FeignClient(
        url = "${app.storage.internal-url}",
        name = "storage",
        contextId = "common-storage",
        configuration = FeignClientConfiguration.class)
public interface StorageClient {
    @PostMapping(value = "/api/v1.0.0/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<List<FileResponse>> uploadPrivateFiles(@RequestPart("files") List<MultipartFile> files,
                                                       @RequestParam("ownerId") String ownerId) throws IOException ;

    @PostMapping(value = "/api/v1.0.0/public/files/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<List<FileResponse>> uploadPublicFile(@RequestPart("files") List<MultipartFile> files,
                                          @RequestParam("ownerId") String ownerId) throws IOException;

    @GetMapping("/api/v1.0.0/files/{fileId}")
    ResponseEntity<byte[]> downloadPrivateFile(@PathVariable String fileId,
                                               @RequestParam(required = false) Integer width,
                                               @RequestParam(required = false) Integer height,
                                               @RequestParam(required = false) Double ratio) throws IOException;
    @GetMapping("/api/v1.0.0/public/files/{fileId}")
    ResponseEntity<byte[]> downloadPublicFile(@PathVariable String fileId,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) throws IOException;

    @DeleteMapping("/api/v1.0.0/public/files/{fileId}")
    ApiResponse<Boolean> deletePublicFile(@PathVariable String fileId);

    @DeleteMapping("/api/v1.0.0/files/{fileId}")
    ApiResponse<Boolean> deletePrivateFile(@PathVariable String fileId);

    @GetMapping("/api/v1.0.0/files/{fileId}/info")
    ApiResponse<FileResponse> getPrivateFileInfo(@PathVariable String fileId);

    @GetMapping("/api/v1.0.0/public/files/{fileId}/info")
    ApiResponse<FileResponse> getPublicFileInfo(@PathVariable String fileId);

    @GetMapping("/api/v1.0.0/files")
    ApiResponse<PageResponse<FileResponse>> getFiles(@SpringQueryMap FilePageRequest request);
}
