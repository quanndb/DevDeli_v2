package com.devdeli.common.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FileActionPageRequest extends PageRequest{
    private String id;
    private String fileId;
    private String action;
    private LocalDate createdDate;
    private String createdBy;
    private LocalDate updatedDate;
    private String updatedBy;
}
