package com.wanted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.domain.user.dto.UserLoginRequest;
import com.wanted.domain.user.dto.UserLoginResponse;
import com.wanted.global.aop.BindingCheckAop;
import com.wanted.global.config.SecurityConfig;
import com.wanted.global.exception.AppException;
import com.wanted.global.exception.ErrorCode;
import com.wanted.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static com.wanted.global.constants.ValidationConstants.EMAIL_BINDING_ERROR_MESSAGE;
import static com.wanted.global.constants.ValidationConstants.PASSWORD_BINDING_ERROR_MESSAGE;
import static com.wanted.global.exception.ErrorCode.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserApiController.class)
@EnableAspectJAutoProxy
@Import({SecurityConfig.class, BindingCheckAop.class})
class UserApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    @MockBean
    private UserService userService;

    String email = "email@email.com";
    String password = "password";
    Long userId = 1L;
    String jwt = "jwt";
    UserCreateRequest userCreateRequest;
    UserCreateResponse userCreateResponse;
    UserLoginRequest userLoginRequest;
    UserLoginResponse userLoginResponse;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        userCreateRequest = UserCreateRequest.builder()
                .email(email)
                .password(password)
                .build();

        userCreateResponse = UserCreateResponse.builder()
                .userId(userId)
                .email(email)
                .build();

        userLoginRequest = UserLoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        userLoginResponse = UserLoginResponse.builder()
                .userId(userId)
                .jwt(jwt)
                .build();
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class CreateUser {

        @Test
        @DisplayName("성공")
        void createUser_success() throws Exception {
            given(userService.createUser(userCreateRequest))
                    .willReturn(userCreateResponse);

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.email").value(email));
        }


        @Test
        @DisplayName("실패 - 이미 가입된 이메일로 요청 시")
        void createUser_fail_duplicateEmail() throws Exception {
            when(userService.createUser(userCreateRequest))
                    .thenThrow(new AppException(DUPLICATE_EMAIL));

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("이미 존재하는 이메일 입니다."));
        }

        private static Stream<Arguments> createUserFailScenarios() {
            return Stream.of(
                    Arguments.of(EMAIL_BINDING_ERROR_MESSAGE, UserCreateRequest.builder().email("email").password("12345678").build()),
                    Arguments.of(PASSWORD_BINDING_ERROR_MESSAGE, UserCreateRequest.builder().email("email@email.com").password("1234567").build())
            );
        }

        @DisplayName("Request Dto 유효성 검증 실패")
        @ParameterizedTest
        @MethodSource("createUserFailScenarios")
        void createUser_fail_bindingError(String errorMessage, UserCreateRequest request) throws Exception {

            mockMvc.perform(post("/api/v1/users")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }


    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginUser {

        @Test
        @DisplayName("성공")
        void loginUser_success() throws Exception {
            given(userService.loginUser(userLoginRequest))
                    .willReturn(userLoginResponse);

            mockMvc.perform(post("/api/v1/users/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userLoginRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.jwt").value(jwt));
        }

        private static Stream<Arguments> loginUserFailScenarios() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND,404,"가입된 회원을 찾을 수 없습니다."),
                    Arguments.of(WRONG_PASSWORD,401,"비밀번호가 일치하지 않습니다.")
            );
        }

        @DisplayName("실패")
        @ParameterizedTest
        @MethodSource("loginUserFailScenarios")
        void loginUser_fail_exception(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(userService.loginUser(userLoginRequest))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(post("/api/v1/users/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userLoginRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }


        private static Stream<Arguments> loginUserBindingFailScenarios() {
            return Stream.of(
                    Arguments.of(EMAIL_BINDING_ERROR_MESSAGE, UserLoginRequest.builder().email("email").password("12345678").build()),
                    Arguments.of(PASSWORD_BINDING_ERROR_MESSAGE, UserLoginRequest.builder().email("email@email.com").password("1234567").build())
            );
        }

        @DisplayName("Request Dto 유효성 검증 실패")
        @ParameterizedTest
        @MethodSource("loginUserBindingFailScenarios")
        void loginUser_fail_bindingError(String errorMessage, UserLoginRequest request) throws Exception {

            mockMvc.perform(post("/api/v1/users/login")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }


    }

}