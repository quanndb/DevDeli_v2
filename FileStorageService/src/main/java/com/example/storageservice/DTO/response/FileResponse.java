package com.example.storageservice.DTO.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileResponse {
    private String id;
    private String ownerId;
    private String name;
    private String path;
    private String type;
    private long size;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private boolean sharing;
}
