package com.wanted.service;


import com.wanted.domain.post.Post;
import com.wanted.domain.post.PostRepository;
import com.wanted.domain.post.dto.*;
import com.wanted.domain.user.User;
import com.wanted.domain.user.UserRepository;
import com.wanted.global.exception.AppException;
import com.wanted.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public PostCreateResponse createPost(PostCreateRequest requestDto, String email) {
        User foundUser = validateAndFindUserByEmail(email);

        Post savedPost = postRepository.save(requestDto.toEntity(foundUser));

        return PostCreateResponse.from(savedPost);
    }



    public PostGetResponse getPost(Long postId) {
        return PostGetResponse.from(validateAndFindPostById(postId));
    }



    public Page<PostGetResponse> getPostPage(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(post -> PostGetResponse.from(post));
    }

    public PostUpdateResponse updatePost(PostUpdateRequest requestDto, Long postId, String email) {
        User foundUser = validateAndFindUserByEmail(email);

        Post foundPost = validateAndFindPostById(postId);

        if (!foundPost.checkUser(foundUser)) {
            throw new AppException(ErrorCode.USER_NOT_MATCH);
        }

        foundPost.update(requestDto.getTitle(), requestDto.getBody());

        return PostUpdateResponse.from(foundPost);
    }

    private User validateAndFindUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private Post validateAndFindPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }
}
