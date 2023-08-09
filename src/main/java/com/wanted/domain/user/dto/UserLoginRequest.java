package com.wanted.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static com.wanted.global.constants.ValidationConstants.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class UserLoginRequest {
    @Email(message = EMAIL_BINDING_ERROR_MESSAGE)
    private String email;

    @Length(min = PASSWORD_REQUIRED_LENGTH, message = PASSWORD_BINDING_ERROR_MESSAGE)
    private String password;
}
