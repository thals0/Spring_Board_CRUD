package com.sparta.board.dto;

import com.sparta.board.entity.User;
import lombok.Getter;

@Getter
public class SignupResponsDto {
    private String username;
    private String password;

    public SignupResponsDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
