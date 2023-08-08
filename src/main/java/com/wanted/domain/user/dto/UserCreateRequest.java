package com.wanted.domain.user.dto;

import com.wanted.domain.user.User;
import com.wanted.global.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static com.wanted.global.constants.ValidationConstants.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCreateRequest {

    @Email(message = EMAIL_BINDING_ERROR_MESSAGE)
    private String email;

    @Length(min = PASSWORD_REQUIRED_LENGTH, message = PASSWORD_BINDING_ERROR_MESSAGE)
    private String password;

    public User toEntity(String encryptedPassword) {
        return User.builder()
                .email(this.email)
                .password(encryptedPassword)
                .build();
    }
}
