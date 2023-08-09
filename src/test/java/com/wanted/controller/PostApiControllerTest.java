package com.wanted.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.domain.post.dto.*;
import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.domain.user.dto.UserLoginRequest;
import com.wanted.domain.user.dto.UserLoginResponse;
import com.wanted.global.config.SecurityConfig;
import com.wanted.global.exception.AppException;
import com.wanted.global.exception.ErrorCode;
import com.wanted.global.util.JwtUtil;
import com.wanted.service.PostService;
import com.wanted.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Stream;

import static com.wanted.global.exception.ErrorCode.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PostApiController.class)
@Import({SecurityConfig.class})
class PostApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext wac;
    @MockBean
    private PostService postService;
    Long postId = 1L;
    Long userId = 1L;
    String title = "title";
    String body = "body";
    String email = "email";

    @Value("${jwt.secret}")
    String secretKey;
    String token;

    PostCreateRequest postCreateRequest;
    PostCreateResponse postCreateResponse;
    PostGetResponse postGetResponse;
    PostUpdateRequest postUpdateRequest;
    PostUpdateResponse postUpdateResponse;
    Pageable pageable = PageRequest.of(0,20);


    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .body(body)
                .build();

        postCreateResponse = PostCreateResponse.builder()
                .postId(postId)
                .userId(userId)
                .title(title)
                .body(body)
                .build();

        postGetResponse = PostGetResponse.builder()
                .postId(postId)
                .author(email)
                .title(title)
                .body(body)
                .build();

        postUpdateRequest = PostUpdateRequest.builder()
                .title(title)
                .body(body)
                .build();

        postUpdateResponse = PostUpdateResponse.builder()
                .postId(postId)
                .title(title)
                .body(body)
                .build();

        token = JwtUtil.createToken(email, secretKey);

    }

    @Nested
    @DisplayName("게시글 등록 테스트")
    class CreatePost {

        @Test
        @DisplayName("성공")
        void createPost_success() throws Exception {

            given(postService.createPost(postCreateRequest,email))
                    .willReturn(postCreateResponse);

            mockMvc.perform(post("/api/v1/posts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andExpect(jsonPath("$.result.userId").value(userId))
                    .andExpect(jsonPath("$.result.title").value(title))
                    .andExpect(jsonPath("$.result.body").value(body));
        }

        @Test
        @DisplayName("실패 - 가입된 회원이 아닌 경우")
        void createPost_error_userNotFound() throws Exception {

            when(postService.createPost(postCreateRequest,email))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("가입된 회원을 찾을 수 없습니다."));
        }

        @Test
        @DisplayName("실패 - jwt 없이 요청 시")
        void createPost_error_tokenNotFound() throws Exception {

            mockMvc.perform(post("/api/v1/posts")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("유효한 토큰이 아닙니다."));
        }
    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class GetPost {

        @Test
        @DisplayName("성공")
        void getPost_success() throws Exception {
            given(postService.getPost(postId))
                    .willReturn(postGetResponse);

            mockMvc.perform(get("/api/v1/posts/" + postId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andExpect(jsonPath("$.result.author").value(email))
                    .andExpect(jsonPath("$.result.title").value(title))
                    .andExpect(jsonPath("$.result.body").value(body));
        }


        @Test
        @DisplayName("실패 - postId에 해당하는 게시글이 존재하지 않는 경우")
        void getPost_fail() throws Exception {
            when(postService.getPost(postId))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(get("/api/v1/posts/" + postId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("게시글을 찾을 수 없습니다."));

        }

        @Test
        @DisplayName("성공")
        void getPostPage_success() throws Exception {
            given(postService.getPostPage(pageable))
                    .willReturn(new PageImpl<>(List.of(postGetResponse)));

            mockMvc.perform(get("/api/v1/posts"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content[0].postId").value(postId))
                    .andExpect(jsonPath("$.result.content[0].author").value(email))
                    .andExpect(jsonPath("$.result.content[0].title").value(title))
                    .andExpect(jsonPath("$.result.content[0].body").value(body));
        }

    }


    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePost {

        @Test
        @DisplayName("성공")
        void updatePost_success() throws Exception {

            given(postService.updatePost(postUpdateRequest, postId, email))
                    .willReturn(postUpdateResponse);

            mockMvc.perform(put("/api/v1/posts/"+postId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId))
                    .andExpect(jsonPath("$.result.title").value(title))
                    .andExpect(jsonPath("$.result.body").value(body));
        }

        private static Stream<Arguments> updatePostFailScenarios() {
            return Stream.of(
                    Arguments.of(USER_NOT_FOUND,404,"가입된 회원을 찾을 수 없습니다."),
                    Arguments.of(POST_NOT_FOUND,404,"게시글을 찾을 수 없습니다."),
                    Arguments.of(USER_NOT_MATCH,401,"작성자 본인만 요청할 수 있습니다.")
            );
        }

        @DisplayName("실패")
        @ParameterizedTest
        @MethodSource("updatePostFailScenarios")
        void updatePost_fail_exception(ErrorCode errorCode, int responseStatus, String errorMessage) throws Exception {
            when(postService.updatePost(postUpdateRequest,postId,email))
                    .thenThrow(new AppException(errorCode));

            mockMvc.perform(put("/api/v1/posts/"+postId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().is(responseStatus))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value(errorMessage));

        }


        @Test
        @DisplayName("실패 - jwt 없이 요청 시")
        void updatePost_error_tokenNotFound() throws Exception {

            mockMvc.perform(put("/api/v1/posts/"+postId)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result").value("유효한 토큰이 아닙니다."));
        }
    }
}