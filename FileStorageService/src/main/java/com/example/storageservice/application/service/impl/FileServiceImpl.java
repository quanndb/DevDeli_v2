package com.example.storageservice.application.service.impl;

import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.dto.response.PageResponse;
import com.devdeli.common.service.FileService;
import com.example.storageservice.application.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStorageService fileStorageService;

    @Override
    public List<FileResponse> uploadPrivateFiles(List<MultipartFile> files, String ownerId) throws IOException {
        return fileStorageService.storeFiles(ownerId, false, files);
    }

    @Override
    public List<FileResponse> uploadPublicFiles(List<MultipartFile> files, String ownerId) throws IOException {
        return fileStorageService.storeFiles(ownerId, true, files);
    }

    @Override
    public ResponseEntity<byte[]> downloadPublicFile(String fileId, Integer width, Integer height, Double ratio) throws IOException {
        return fileStorageService.loadFileAsResource(fileId, true, width, height, ratio);
    }

    @Override
    public ResponseEntity<byte[]>  downloadPrivateFile(String fileId, Integer width, Integer height, Double ratio) throws IOException {
        return fileStorageService.loadFileAsResource(fileId, false, width, height, ratio);
    }

    @Override
    public Boolean deletePublicFile(String fileId) {
        return fileStorageService.deleteFile(fileId, true);
    }

    @Override
    public Boolean deletePrivateFile(String fileId) {
        return fileStorageService.deleteFile(fileId, false);
    }

    @Override
    public PageResponse<FileResponse> getFiles(FilePageRequest request) {
        return fileStorageService.getFiles(request);
    }

    @Override
    public FileResponse getPublicFileInfo(String fileId) {
        return fileStorageService.getFileInfo(fileId, true);
    }

    @Override
    public FileResponse getPrivateFileInfo(String fileId) {
        return fileStorageService.getFileInfo(fileId, false);
    }
}
