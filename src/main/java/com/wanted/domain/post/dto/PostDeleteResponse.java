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
public class PostDeleteResponse {
    private Long postId;
    private String title;
    private String body;

    public static PostDeleteResponse from(Post post) {
        return PostDeleteResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .build();
    }

}
