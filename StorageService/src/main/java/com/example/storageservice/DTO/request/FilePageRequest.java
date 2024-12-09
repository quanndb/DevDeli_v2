package com.example.storageservice.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilePageRequest extends PageRequest{
    private String id;
    private String ownerId;
    private String name;
    private String path;
    private String type;
    private boolean sharing;
}
