package com.wanted.service;


import com.wanted.domain.post.Post;
import com.wanted.domain.post.PostRepository;
import com.wanted.domain.post.dto.PostCreateRequest;
import com.wanted.domain.post.dto.PostCreateResponse;
import com.wanted.domain.user.User;
import com.wanted.domain.user.UserRepository;
import com.wanted.global.exception.AppException;
import com.wanted.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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

    private User validateAndFindUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
