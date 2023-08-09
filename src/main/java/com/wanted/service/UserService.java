package com.wanted.service;

import com.wanted.domain.user.User;
import com.wanted.domain.user.UserRepository;
import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.domain.user.dto.UserLoginRequest;
import com.wanted.domain.user.dto.UserLoginResponse;
import com.wanted.global.exception.AppException;
import com.wanted.global.exception.ErrorCode;
import com.wanted.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encryption;

    /**
     * 회원 가입
     */
    public UserCreateResponse createUser(UserCreateRequest requestDto) {
        // 이메일 중복 확인
        checkDuplicateEmail(requestDto.getEmail());

        // 비밀번호 암호화
        String encryptedPassword = encryption.encode(requestDto.getPassword());

        User savedUser = userRepository.save(requestDto.toEntity(encryptedPassword));

        return UserCreateResponse.from(savedUser);
    }

    /**
     * 이메일이 중복되는지 확인, 중복되는 경우 에러 처리
     */
    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public UserLoginResponse loginUser(UserLoginRequest request) {

        User foundUser = validateAndFindUserByEmail(request.getEmail());

        validatePassword(request, foundUser);

        return UserLoginResponse.from(foundUser, JwtUtil.createToken(foundUser.getEmail(), secretKey));
    }

    private User validateAndFindUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void validatePassword(UserLoginRequest request, User foundUser) {
        if (!encryption.matches(request.getPassword(), foundUser.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
    }


}
