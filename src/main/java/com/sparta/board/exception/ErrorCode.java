package com.sparta.board.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_TOKEN(BAD_REQUEST, "토큰이 유효하지 않습니다"),
    DUPLICATE_USER(BAD_REQUEST, "중복된 사용자가 존재합니다"),
    NOT_PROPER_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_AUTHOR(BAD_REQUEST, "작성자만 삭제/수정할 수 있습니다."),
    WRONG_ADMIN_TOKEN(BAD_REQUEST, "관리자 암호가 틀려 등록이 불가능합니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "등록된 사용자가 없습니다"),
    POST_NOT_FOUND(NOT_FOUND, "선택한 게시물을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(NOT_FOUND, "선택한 댓글을 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),

    ;

    private final HttpStatus httpStatus;
    private final String detail;


}
