package com.example.storageservice.service.impl;

import com.devdeli.common.service.ResourceCheckerService;
import com.example.storageservice.exception.AppExceptions;
import com.example.storageservice.exception.ErrorCode;
import com.example.storageservice.repository.FileRepository;
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
