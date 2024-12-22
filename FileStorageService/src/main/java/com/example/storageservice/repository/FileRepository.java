package com.example.storageservice.repository;

import com.example.storageservice.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String>, CustomFileRepository {

    Optional<File> findByPathAndDeletedIsFalse(String fileName);

    default List<File> saveAllFiles(List<File> request){
        saveFileImportAction(request);
        return saveAll(request);
    }

    default Optional<File> getFile(String filePath){
        saveFileExportAction(filePath);
        return findByPathAndDeletedIsFalse(filePath);
    }
}
