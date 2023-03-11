package com.sparta.board.dto;

import lombok.Getter;

@Getter
public class StatusCodeDto {
    private int statusCode;
    private String msg;

    public StatusCodeDto(int statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
    }
}
