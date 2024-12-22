package com.devdeli.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(501, "Invalid key", HttpStatus.INTERNAL_SERVER_ERROR),
    CANT_CREATE_DIR(502, "Could not create the directory where the uploaded files will be stored", HttpStatus.INTERNAL_SERVER_ERROR),
    CANT_SAVE_FILE(502, "There are some errors while save file", HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_EXCEPTION(403,"You're unable to do this", HttpStatus.FORBIDDEN),
    UNKNOWN_REQUEST(404, "Unknown request has been requested", HttpStatus.BAD_REQUEST),
    ;

    private final int code;
    private final String message;
    private final HttpStatus statusCode;
}
