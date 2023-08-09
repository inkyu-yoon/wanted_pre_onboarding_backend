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
public class UserLoginResponse {
    private Long userId;
    private String jwt;

    public static UserLoginResponse from(User user, String jwt) {
        return UserLoginResponse.builder()
                .userId(user.getId())
                .jwt(jwt)
                .build();
    }
}
