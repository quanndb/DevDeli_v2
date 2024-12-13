package com.example.storageservice.repository;

import com.example.storageservice.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String>, CustomFileRepository {

    Optional<File> findByPathAndDeletedIsFalse(String fileName);
}
