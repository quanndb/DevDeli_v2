package com.example.storageservice.repository;

import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.FileResponse;

import java.util.List;

public interface CustomFileRepository {
    List<FileResponse> search(FilePageRequest request);
    Long count(FilePageRequest request);
}
