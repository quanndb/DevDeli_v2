package com.example.storageservice.mapper;

import com.example.storageservice.DTO.response.FileResponse;
import com.example.storageservice.entity.File;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

    FileResponse toFileResponse(File file);

    List<FileResponse> toListFileResponse(List<File> files);
}