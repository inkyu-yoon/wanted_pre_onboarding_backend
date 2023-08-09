package com.wanted.service;

import com.wanted.domain.user.User;
import com.wanted.domain.user.UserRepository;
import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.domain.user.dto.UserLoginRequest;
import com.wanted.domain.user.dto.UserLoginResponse;
import com.wanted.global.exception.AppException;
import com.wanted.global.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.wanted.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

    @Value("${jwt.secret}")
    private String secretKey;

    Long userId = 1L;
    String email = "email";
    String password = "password";
    String encryptedPassword = "encryptedPassword";
    String jwt = "jwt";
    UserCreateRequest userCreateRequest;
    UserLoginRequest userLoginRequest;

    @BeforeEach
    void setUp() {
        userCreateRequest = UserCreateRequest.builder()
                .email(email)
                .password(password)
                .build();

        userLoginRequest = UserLoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    @Nested
    @DisplayName("사용자 등록 테스트")
    class CreateUser {

        @Test
        @DisplayName("성공")
        void createUser_success() {
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
        void createUser_fail() {
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

    @Nested
    @DisplayName("회원 로그인 테스트")
    class LoginUser {

        @Test
        @DisplayName("회원 로그인 성공 테스트")
        public void loginUser_success() {
            MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class);

            given(userRepository.findByEmail(userLoginRequest.getEmail()))
                    .willReturn(Optional.of(mockUser));
            given(mockUser.getPassword())
                    .willReturn(encryptedPassword);
            given(encoder.matches(password, mockUser.getPassword()))
                    .willReturn(true);
            given(mockUser.getId())
                    .willReturn(userId);
            given(JwtUtil.createToken(mockUser.getEmail(),secretKey))
                    .willReturn(jwt);

            UserLoginResponse response = userService.loginUser(userLoginRequest);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(userId);
            assertThat(response.getJwt()).isEqualTo(jwt);
            verify(userRepository, atLeastOnce()).findByEmail(userLoginRequest.getEmail());
            verify(mockUser, atLeastOnce()).getPassword();
            verify(encoder, atLeastOnce()).matches(userLoginRequest.getPassword(), encryptedPassword);

            jwtUtil.close();
        }

        @Test
        @DisplayName("가입되지 않은 회원인 경우 예외 발생")
        public void loginUser_error_userNotFound() {
            when(userRepository.findByEmail(userLoginRequest.getEmail()))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> userService.loginUser(userLoginRequest));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(userLoginRequest.getEmail());

        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 예외 발생")
        public void loginUser_error_wrongPassword() {
            given(userRepository.findByEmail(userLoginRequest.getEmail()))
                    .willReturn(Optional.of(mockUser));
            given(mockUser.getPassword())
                    .willReturn(encryptedPassword);
            given(encoder.matches(password, mockUser.getPassword()))
                    .willReturn(false);

            AppException appException = assertThrows(AppException.class, () -> userService.loginUser(userLoginRequest));

            assertThat(appException.getErrorCode()).isEqualTo(WRONG_PASSWORD);

            verify(userRepository, atLeastOnce()).findByEmail(userLoginRequest.getEmail());

        }
    }

}