package com.devdeli.common.service;

import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.dto.response.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<FileResponse> uploadPrivateFiles(List<MultipartFile> files, String ownerId) throws IOException;
    List<FileResponse> uploadPublicFiles(List<MultipartFile> files, String ownerId) throws IOException;
    ResponseEntity<byte[]> downloadPublicFile(String fileId, Integer width, Integer height, Double ratio) throws IOException;
    ResponseEntity<byte[]> downloadPrivateFile(String fileId, Integer width, Integer height, Double ratio) throws IOException;
    Boolean deletePublicFile(String fileId);
    Boolean deletePrivateFile(String fileId);
    PageResponse<FileResponse> getFiles(FilePageRequest request);
    FileResponse getPublicFileInfo(String fileId);
    FileResponse getPrivateFileInfo(String fileId);
}
