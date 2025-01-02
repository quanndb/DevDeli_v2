package com.example.storageservice.domain.repository;

import com.example.storageservice.domain.FileAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileActionRepository extends JpaRepository<FileAction, UUID>, CustomFileActionRepository {
}
