package com.devdeli.common.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FilePageRequest extends PageRequest {
    private String id;
    private String ownerId;
    private String name;
    private String path;
    private String type;
    private Boolean sharing;
    private LocalDate createdDate;
}
