package com.wanted.domain.post.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostUpdateRequest {
    private String title;
    private String body;
}
