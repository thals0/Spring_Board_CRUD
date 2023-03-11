package com.sparta.board.dto;

import com.sparta.board.entity.User;
import lombok.Getter;

@Getter
public class SignupResponseDto {
    private String username;
    private String password;

    public SignupResponseDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
