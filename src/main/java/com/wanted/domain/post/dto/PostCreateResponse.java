package com.wanted.domain.post.dto;

import com.wanted.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponse {
    private Long userId;
    private Long postId;
    private String title;
    private String body;


    public static PostCreateResponse from(Post post) {
        return PostCreateResponse.builder()
                .userId(post.getUser().getId())
                .postId(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .build();
    }

}
