package com.wanted.service;

import com.wanted.domain.post.Post;
import com.wanted.domain.post.PostRepository;
import com.wanted.domain.post.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.wanted.global.exception.ErrorCode.*;
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
    Pageable pageable = PageRequest.of(0,20);


    PostCreateRequest postCreateRequest;
    PostUpdateRequest postUpdateRequest;

    @BeforeEach
    void setUp() {
        postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .body(body)
                .build();

        postUpdateRequest = PostUpdateRequest.builder()
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
        @DisplayName("실패 - 가입된 회원이 아닌 경우 예외 발생")
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

    @Nested
    @DisplayName("게시글 조회 테스트")
    class GetPost {

        @Test
        @DisplayName("단건 조회 성공")
        void getPost_success() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(mockUser.getEmail())
                    .willReturn(email);
            given(mockPost.getId())
                    .willReturn(postId);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(mockPost.getBody())
                    .willReturn(body);


            //when
            PostGetResponse response = postService.getPost(postId);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getAuthor()).isEqualTo(email);
            assertThat(response.getTitle()).isEqualTo(title);
            assertThat(response.getBody()).isEqualTo(body);
            verify(postRepository, atLeastOnce()).findById(postId);
        }


        @Test
        @DisplayName("단건 조회 실패 - 존재하지 않은 postId 조회 요청 시 실패")
        void getPost_fail() {
            //given
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.getPost(postId));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("목록 조회 성공")
        void getPostPage_success() {
            //given
            given(postRepository.findAll(pageable))
                    .willReturn(new PageImpl<>(List.of(mockPost)));
            given(mockPost.getUser())
                    .willReturn(mockUser);
            given(mockUser.getEmail())
                    .willReturn(email);
            given(mockPost.getId())
                    .willReturn(postId);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(mockPost.getBody())
                    .willReturn(body);


            //when
            Page<PostGetResponse> response = postService.getPostPage(pageable);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getTotalElements()).isEqualTo(1);
            verify(postRepository, atLeastOnce()).findAll(pageable);
        }

    }


    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePost {

        @Test
        @DisplayName("성공")
        void updatePost_success() {
            //given
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.checkUser(mockUser))
                    .willReturn(true);
            given(mockPost.getId())
                    .willReturn(postId);
            given(mockPost.getTitle())
                    .willReturn(title);
            given(mockPost.getBody())
                    .willReturn(body);


            //when
            PostUpdateResponse response = postService.updatePost(postUpdateRequest, postId, email);

            //then
            assertThat(response).isNotNull();
            assertThat(response.getPostId()).isEqualTo(postId);
            assertThat(response.getTitle()).isEqualTo(title);
            assertThat(response.getBody()).isEqualTo(body);
            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("실패 - 가입된 회원이 아닌 경우 예외 발생")
        void updatePost_fail_userNotFound() {
            //given
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postUpdateRequest, postId, email));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("실패 - postId에 해당하는 게시글이 존재하지 않을 시 예외 발생")
        void updatePost_fail_postNotFound() {
            //given
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postUpdateRequest, postId, email));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("실패 - 수정 요청자와 작성자가 일치하지 않는 경우 예외 발생")
        void updatePost_fail_userNotMatch() {
            //given
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(mockPost.checkUser(mockUser))
                    .willReturn(false);

            //when
            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postUpdateRequest, postId, email));

            //then
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_MATCH);
            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(postRepository, atLeastOnce()).findById(postId);
        }
    }


}