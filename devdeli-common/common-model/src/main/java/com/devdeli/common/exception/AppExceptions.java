package com.devdeli.common.exception;

import com.devdeli.common.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppExceptions extends RuntimeException{

    private ErrorCode errorCode;

    public AppExceptions(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
