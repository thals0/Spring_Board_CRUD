package com.sparta.board.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        restApiException.setErrorMessage(ex.getMessage());

        return new ResponseEntity(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    // TODO : 커스텀 한 예외처리
    @ExceptionHandler(value = { CustomException.class })
    public ResponseEntity<Object>  CustomhandleApiRequestException(CustomException ex) {
        // ExceptionDto는 화면에 보여줄 내용을 정의
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(new CustomExceptionDto(ex));
    }
}