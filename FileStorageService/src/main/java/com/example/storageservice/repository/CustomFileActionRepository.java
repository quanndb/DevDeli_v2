package com.example.storageservice.repository;

import com.devdeli.common.dto.request.FileActionPageRequest;
import com.example.storageservice.entity.FileAction;

import java.util.List;

public interface CustomFileActionRepository {
    List<FileAction> search(FileActionPageRequest request);
    Long count(FileActionPageRequest request);
}
