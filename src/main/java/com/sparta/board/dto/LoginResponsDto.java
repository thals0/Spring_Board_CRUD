package com.sparta.board.dto;

import lombok.Getter;

@Getter
public class LoginResponsDto {
    private String username;
    private String password;

    public LoginResponsDto(LoginRequestDto loginRequestDto) {
        this.username = loginRequestDto.getUsername();
        this.password = loginRequestDto.getPassword();
    }
}
