package com.wanted.domain.user.dto;

import com.wanted.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    private Long userId;
    private String email;

    public static UserCreateResponse from(User user) {
        return UserCreateResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
