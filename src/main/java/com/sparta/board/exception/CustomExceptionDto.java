package com.sparta.board.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomExceptionDto {
    private int errorCode;
    private HttpStatus httpStatus;
    private String msg;

    public CustomExceptionDto(CustomException ex) {
        this.errorCode = ex.getErrorCode().getHttpStatus().value();
        this.httpStatus = ex.getErrorCode().getHttpStatus();
        this.msg = ex.getErrorCode().getDetail();
    }
}
