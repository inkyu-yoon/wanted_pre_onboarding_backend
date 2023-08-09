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
public class PostUpdateResponse {
    private Long postId;
    private String title;
    private String body;

    public static PostUpdateResponse from(Post post) {
        return PostUpdateResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .build();
    }

}
