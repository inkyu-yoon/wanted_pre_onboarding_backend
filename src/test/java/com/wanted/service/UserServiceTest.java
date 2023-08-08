package com.wanted.service;

import com.wanted.domain.user.User;
import com.wanted.domain.user.UserRepository;
import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.global.exception.AppException;
import com.wanted.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.wanted.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    String email = "email";
    String password = "password";
    String encryptedPassword = "encryptedPassword";
    UserCreateRequest userCreateRequest;

    @BeforeEach
    void setUp() {
        userCreateRequest = UserCreateRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    @Nested
    @DisplayName("사용자 등록 테스트")
    class CreateUser{

        @Test
        @DisplayName("성공")
        void createUser_success(){
            //given
            given(userRepository.existsByEmail(email))
                    .willReturn(false);
            given(encoder.encode(password))
                    .willReturn(encryptedPassword);
            given(userRepository.save(any(User.class)))
                    .willReturn(mockUser);
            given(mockUser.getEmail())
                    .willReturn(email);

            //when
            UserCreateResponse response = userService.createUser(userCreateRequest);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getEmail()).isEqualTo(email);
            verify(userRepository, atLeastOnce()).existsByEmail(email);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("사용자 명이 이미 존재하는 경우, 사용자 등록 시 중복 예외가 발생")
        void createUser_fail(){
            //given
            given(userRepository.existsByEmail(email))
                    .willReturn(true);

            //when
            AppException appException = assertThrows(AppException.class, () -> userService.createUser(userCreateRequest));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(DUPLICATE_EMAIL);
            verify(userRepository, atLeastOnce()).existsByEmail(email);
        }
    }
}