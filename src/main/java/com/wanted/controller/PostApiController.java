package com.wanted.controller;

import com.wanted.domain.post.dto.*;
import com.wanted.global.Response;
import com.wanted.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostGetResponse>> get(@PathVariable(name = "postId") Long postId) {

        PostGetResponse response = postService.getPost(postId);

        return ResponseEntity.ok(Response.success(response));
    }

    @GetMapping
    public ResponseEntity<Response<Page<PostGetResponse>>> getPage(Pageable pageable) {

        Page<PostGetResponse> response = postService.getPostPage(pageable);

        return ResponseEntity.ok(Response.success(response));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Response<PostUpdateResponse>> update(@RequestBody PostUpdateRequest requestDto, Authentication authentication, @PathVariable(name = "postId") Long postId) {

        PostUpdateResponse response = postService.updatePost(requestDto, postId, authentication.getName());

        return ResponseEntity.ok(Response.success(response));
    }


}
