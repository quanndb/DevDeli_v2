package com.example.storageservice.repository;

import com.example.storageservice.DTO.request.FilePageRequest;
import com.example.storageservice.DTO.response.FileResponse;

import java.util.List;

public interface CustomFileRepository {
    List<FileResponse> search(FilePageRequest request);
    Long count(FilePageRequest request);
}
