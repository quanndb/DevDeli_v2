package com.devdeli.common.security.impl;

import com.devdeli.common.client.storage.StorageClient;
import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.dto.response.PageResponse;
import com.devdeli.common.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoteFileServiceImpl implements FileService {

    private final StorageClient storageClient;

    @Override
    public List<FileResponse> uploadPrivateFiles(List<MultipartFile> files, String ownerId) throws IOException {
        var res =  storageClient.uploadPrivateFiles(files,ownerId);
        return res.getResult();
    }

    @Override
    public List<FileResponse> uploadPublicFiles(List<MultipartFile> files, String ownerId) throws IOException {
        return storageClient.uploadPublicFile(files,ownerId).getResult();
    }

    @Override
    public ResponseEntity<byte[]> downloadPublicFile(String fileId, Integer width, Integer height, Double ratio) throws IOException {
        return storageClient.downloadPublicFile(fileId, width, height, ratio);
    }

    @Override
    public ResponseEntity<byte[]> downloadPrivateFile(String fileId, Integer width, Integer height, Double ratio) throws IOException {
        return storageClient.downloadPrivateFile(fileId, width, height, ratio);
    }

    @Override
    public Boolean deletePublicFile(String fileId) {
        return storageClient.deletePublicFile(fileId).getResult();
    }

    @Override
    public Boolean deletePrivateFile(String fileId) {
        return storageClient.deletePrivateFile(fileId).getResult();
    }

    @Override
    public PageResponse<FileResponse> getFiles(FilePageRequest request) {
        return storageClient.getFiles(request).getResult();
    }

    @Override
    public FileResponse getPublicFileInfo(String fileId) {
        return storageClient.getPublicFileInfo(fileId).getResult();
    }

    @Override
    public FileResponse getPrivateFileInfo(String fileId) {
        return storageClient.getPrivateFileInfo(fileId).getResult();
    }
}
