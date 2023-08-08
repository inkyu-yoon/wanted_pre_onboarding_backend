package com.wanted.domain.user.dto;

import com.wanted.domain.user.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCreateRequest {

    private String email;

    private String password;

    public User toEntity(String encryptedPassword) {
        return User.builder()
                .email(this.email)
                .password(encryptedPassword)
                .build();
    }
}
