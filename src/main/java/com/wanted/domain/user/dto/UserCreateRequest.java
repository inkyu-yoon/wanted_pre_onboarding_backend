package com.wanted.domain.user.dto;

import com.wanted.domain.user.User;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCreateRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다. '@' 를 포함시켜주세요.")
    private String email;

    @Length(min = 8,message = "비밀번호는 최소 8자 이상입니다.")
    private String password;

    public User toEntity(String encryptedPassword) {
        return User.builder()
                .email(this.email)
                .password(encryptedPassword)
                .build();
    }
}
