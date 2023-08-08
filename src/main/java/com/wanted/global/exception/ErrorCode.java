package com.wanted.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다.");

    private HttpStatus httpStatus;
    private String message;
}
