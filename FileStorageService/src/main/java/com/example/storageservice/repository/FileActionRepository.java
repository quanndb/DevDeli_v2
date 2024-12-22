package com.example.storageservice.repository;

import com.example.storageservice.entity.FileAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileActionRepository extends JpaRepository<FileAction, String>, CustomFileActionRepository {
}
