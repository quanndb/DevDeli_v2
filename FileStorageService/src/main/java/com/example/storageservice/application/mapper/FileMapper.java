package com.example.storageservice.application.mapper;

import com.devdeli.common.dto.response.FileResponse;
import com.example.storageservice.domain.File;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileResponse toFileResponse(File file);

    List<FileResponse> toListFileResponse(List<File> files);
}
