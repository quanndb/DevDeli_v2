package com.example.storageservice.service;

import com.devdeli.common.dto.request.FileActionPageRequest;
import com.devdeli.common.dto.response.PageResponse;
import com.example.storageservice.entity.FileAction;
import com.example.storageservice.repository.FileActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileActionService {

    private final FileActionRepository fileActionRepository;

    public PageResponse<FileAction> getFiles(FileActionPageRequest request) {
        long totalRecords = fileActionRepository.count(request);
        List<FileAction> actionList = fileActionRepository.search(request);
        return PageResponse.<FileAction>builder()
                .page(request.getPage())
                .size(request.getSize())
                .query(request.getQuery())
                .sortedBy(request.getSortedBy())
                .sortDirection(request.getSortDirection().name())
                .first(request.getPage() == 1)
                .last(request.getPage() % request.getSize() == request.getPage())
                .totalRecords(totalRecords)
                .totalPages(request.getPage() % request.getSize())
                .response(actionList)
                .build();
    }
}
