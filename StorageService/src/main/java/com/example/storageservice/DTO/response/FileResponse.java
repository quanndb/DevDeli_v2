package com.example.storageservice.DTO.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {
    private String id;
    private String ownerId;
    private String name;
    private String path;
    private String type;
    private boolean sharing;
}
