package com.example.storageservice.domain.repository;

import com.example.storageservice.domain.FileAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileActionRepository extends JpaRepository<FileAction, String>, CustomFileActionRepository {
}
