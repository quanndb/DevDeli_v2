package com.example.identityService.service.impl;

import com.devdeli.common.service.ResourceCheckerService;
import org.springframework.stereotype.Service;

@Service
public class ProfileCheckerServiceImpl implements ResourceCheckerService {
    @Override
    public String ownerId(String resourceId) {
        return "";
    }
}
