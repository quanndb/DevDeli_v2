package com.example.storageservice.application.service.impl;

import com.devdeli.common.service.ResourceCheckerService;
import com.example.storageservice.application.exception.AppExceptions;
import com.example.storageservice.application.exception.ErrorCode;
import com.example.storageservice.domain.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileOwnerCheckerImpl implements ResourceCheckerService {

    private final FileRepository fileRepository;

    @Override
    public String ownerId(String resourceId) {
        return fileRepository.findByPathAndDeletedIsFalse(resourceId).
            orElseThrow(()-> new AppExceptions(ErrorCode.FILE_NOT_FOUND)).getOwnerId();
    }
}
