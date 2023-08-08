package com.wanted.controller;

import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.global.Response;
import com.wanted.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Response<UserCreateResponse>> create(@RequestBody UserCreateRequest requestDto) {


        UserCreateResponse response = userService.createUser(requestDto);

        return ResponseEntity.ok(Response.success(response));
    }
}

