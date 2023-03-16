package com.sparta.board.controller;

import com.sparta.board.dto.LoginRequestDto;
import com.sparta.board.dto.SignupRequestDto;
import com.sparta.board.entity.User;
import com.sparta.board.exception.RestApiException;
import com.sparta.board.jwt.JwtUtil;
import com.sparta.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 모든 유저 확인
    @GetMapping("/")
    public List<User> getUsers() {
        return userService.getUsers();
    }
    // 회원 가입
    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, BindingResult result){
        if (result.hasErrors()){
            RestApiException restApiException = new RestApiException();
            restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
            restApiException.setErrorMessage(result.getFieldError().getDefaultMessage());

            return new ResponseEntity(
                    restApiException,
                    HttpStatus.BAD_REQUEST
            );
        }
        userService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("회원가입 성공");
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        userService.login(loginRequestDto, response);
        return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
    }

}
