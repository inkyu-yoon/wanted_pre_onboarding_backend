package com.wanted.domain.post.dto;

import com.wanted.domain.post.Post;
import com.wanted.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostCreateRequest {
    private String title;
    private String body;

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .title(this.title)
                .body(this.body)
                .build();
    }

}
