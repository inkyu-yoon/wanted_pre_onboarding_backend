package com.wanted.controller;

import com.wanted.domain.post.dto.PostCreateRequest;
import com.wanted.domain.post.dto.PostCreateResponse;
import com.wanted.global.Response;
import com.wanted.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostApiController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Response<PostCreateResponse>> create(@RequestBody PostCreateRequest requestDto, Authentication authentication) {

        PostCreateResponse postResponse = postService.createPost(requestDto, authentication.getName());

        return ResponseEntity.ok(Response.success(postResponse));
    }

}
