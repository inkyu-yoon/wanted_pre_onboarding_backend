package com.wanted.controller;

import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.domain.user.dto.UserLoginRequest;
import com.wanted.domain.user.dto.UserLoginResponse;
import com.wanted.global.Response;
import com.wanted.global.annotation.BindingCheck;
import com.wanted.global.specification.UserApiSpecification;
import com.wanted.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController implements UserApiSpecification {
    private final UserService userService;

    @PostMapping
    @BindingCheck
    public ResponseEntity<Response<UserCreateResponse>> create(@RequestBody @Validated UserCreateRequest requestDto, BindingResult br) {

        UserCreateResponse response = userService.createUser(requestDto);

        return ResponseEntity.ok(Response.success(response));
    }

    @PostMapping("/login")
    @BindingCheck
    public ResponseEntity<Response<UserLoginResponse>> login(@RequestBody @Validated UserLoginRequest requestDto, BindingResult br) {

        UserLoginResponse response = userService.loginUser(requestDto);

        return ResponseEntity.ok(Response.success(response));
    }

}

