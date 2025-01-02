package com.example.identityService.infrastructure.persistence.entity;

import com.devdeli.common.Auditable;
import com.example.identityService.application.DTO.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "account")
@RequiredArgsConstructor
@AllArgsConstructor
public class AccountEntity extends Auditable {
    @Id
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String fullname;
    @Column(nullable = false)
    private LocalDate dob;
    @Column(nullable = false)
    private int yoe;
    @Column(nullable = false)
    private boolean verified;
    @Column(nullable = false)
    private boolean enable;
    @Column(nullable = false)
    private boolean deleted;
    private Gender gender;
    private String address;
    private String cloudImageId;
    private String cloudImageUrl;
}
