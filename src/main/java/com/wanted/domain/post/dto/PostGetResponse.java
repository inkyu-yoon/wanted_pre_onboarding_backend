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
public class PostGetResponse {
    private Long postId;
    private String author;
    private String title;
    private String body;

    public static PostGetResponse from(Post foundPost) {
        return PostGetResponse.builder()
                .author(foundPost.getUser().getEmail())
                .postId(foundPost.getId())
                .title(foundPost.getTitle())
                .body(foundPost.getBody())
                .build();
    }
}
