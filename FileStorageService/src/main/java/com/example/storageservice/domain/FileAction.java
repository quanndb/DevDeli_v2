package com.example.storageservice.domain;

import com.devdeli.common.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "file_action")
@AllArgsConstructor
@RequiredArgsConstructor
public class FileAction extends Auditable {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID fileId;
    @Column(nullable = false)
    private String action;
    private String note;
}
