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
@Table(name = "file")
@AllArgsConstructor
@RequiredArgsConstructor
public class File extends Auditable {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String ownerId;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = false)
    private String path;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String extension;
    @Column(name = "sharing", nullable = false)
    private boolean sharing;
    @Column(name = "deleted", nullable = false)
    private boolean deleted;
    @Column(nullable = false)
    private long size;
}
