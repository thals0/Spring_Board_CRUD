package com.sparta.board.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String username;
    private String password;

    public LoginResponseDto(LoginRequestDto loginRequestDto) {
        this.username = loginRequestDto.getUsername();
        this.password = loginRequestDto.getPassword();
    }
}
