package com.example.identityService.infrastructure.persistence.mapper;

import com.example.identityService.application.DTO.response.CloudResponse;
import org.mapstruct.Mapper;


import java.util.Map;

@Mapper(componentModel = "spring")
public interface CloudImageMapper {
    default CloudResponse toCloudResponse(Map<?, ?> map) {
        CloudResponse fileMetadata = new CloudResponse();
        if (map.containsKey("url")) {
            fileMetadata.setUrl(String.valueOf(map.get("url")));
        }
        if (map.containsKey("public_id")) {
            fileMetadata.setPublicId(String.valueOf(map.get("public_id")));
        }
        if (map.containsKey("original_filename")) {
            fileMetadata.setOriginalFileName(
                    String.valueOf(map.get("original_filename")));
        }
        if(map.containsKey("format")){
            fileMetadata.setOriginalFileName(
                    fileMetadata.getOriginalFileName()+"."+ map.get("format")
            );
        }
        return fileMetadata;
    }
}
