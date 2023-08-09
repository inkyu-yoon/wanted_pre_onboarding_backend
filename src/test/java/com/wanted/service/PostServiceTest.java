package com.wanted.service;

import com.wanted.domain.post.Post;
import com.wanted.domain.post.PostRepository;
import com.wanted.domain.post.dto.PostCreateRequest;
import com.wanted.domain.post.dto.PostCreateResponse;
import com.wanted.domain.user.User;
import com.wanted.domain.user.UserRepository;
import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.domain.user.dto.UserLoginRequest;
import com.wanted.global.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.wanted.global.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.wanted.global.exception.ErrorCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User mockUser;

    @Mock
    private Post mockPost;

    @InjectMocks
    private PostService postService;
    String title = "title";
    String body = "body";
    String email = "email";
    Long postId = 1L;
    Long userId = 1L;

    PostCreateRequest postCreateRequest;

    @BeforeEach
    void setUp() {
       postCreateRequest = PostCreateRequest.builder()
               .title(title)
               .body(body)
               .build();
    }

    @Nested
    @DisplayName("게시글 등록 테스트")
    class CreatePost {

        @Test
        @DisplayName("성공")
        void createPost_success() {
            //given
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.save(any(Post.class)))
                    .willReturn(mockPost);
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(mockUser.getId())
                    .willReturn(userId);
            given(mockPost.getId())
                    .willReturn(postId);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(mockPost.getBody())
                    .willReturn(body);


            //when
            PostCreateResponse response = postService.createPost(postCreateRequest,email);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getUserId()).isEqualTo(userId);
            assertThat(response.getTitle()).isEqualTo(title);
            assertThat(response.getBody()).isEqualTo(body);
            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(postRepository, atLeastOnce()).save(any(Post.class));
        }

        @Test
        @DisplayName("가입된 회원이 아닌 경우 예외 발생")
        void createPost_fail_userNotFound() {
            //given
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.createPost(postCreateRequest,email));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }


}