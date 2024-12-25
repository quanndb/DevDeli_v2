package com.example.identityService.application.DTO;

import lombok.Getter;

@Getter
public enum EnumSheetHeader {
    STT("STT"),
    USERNAME("Username"),
    FULL_NAME("Họ Tên"),
    DATE_OF_BIRTH("Ngày sinh"),
    ADDRESS("Địa chỉ"),
    YEARS_OF_EXPERIENCE("Số năm kinh nghiệm"),
    GENDER("Giới tính"),
    IMAGE_URL("Image url"),
    IMAGE_ID("Image id"),
    CREATED_DATE("Ngày tạo"),
    CREATED_BY("Người tạo"),
    VERIFIED("Đã xác thực"),
    ACTIVE("Đang hoạt động"),
    ROLE("Vai trò");

    private final String header;

    EnumSheetHeader(String header) {
        this.header = header;
    }
}

