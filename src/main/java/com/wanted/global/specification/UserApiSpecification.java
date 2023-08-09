package com.wanted.global.specification;

import com.wanted.domain.user.dto.UserCreateRequest;
import com.wanted.domain.user.dto.UserCreateResponse;
import com.wanted.domain.user.dto.UserLoginRequest;
import com.wanted.domain.user.dto.UserLoginResponse;
import com.wanted.global.Response;
import com.wanted.global.annotation.BindingCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "회원 정보 관련 API")
public interface UserApiSpecification {

    @Operation(summary = "회원 가입", description = "💡회원 가입을 합니다.<br>🚨이미 등록된 이메일로 중복 가입 요청 시 예외 처리 됩니다." +
            "<br>🚨이메일에 '@'가 포함되어 있지 않거나 비밀번호가 8자 미만인 경우 예외 처리 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"email\":\"email@email.com\",\"password\":\"password\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "❌ Error : 이메일 중복 시", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"이미 존재하는 이메일 입니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "❌ Error : 이메일 · 비밀번호 조건 불만족 시 ", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping
    @BindingCheck
    ResponseEntity<Response<UserCreateResponse>> create(@RequestBody @Validated UserCreateRequest requestDto, BindingResult br);

    @Operation(summary = "로그인", description = "💡로그인을 합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 비밀번호가 일치하지 않을 시 예외 처리 됩니다." +
            "<br>🚨이메일에 '@'가 포함되어 있지 않거나 비밀번호가 8자 미만인 경우 예외 처리 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\": {\n" +
                            "        \"userId\": 1,\n" +
                            "        \"jwt\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2OTE1NjAxMjUsImV4cCI6MTY5MTU2MzcyNX0.NYqFUMHrSd_LPuSA6I80keJhQgvny6_iOdZ0mBNtckc\"\n" +
                            "    }}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (비밀번호가 일치하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"비밀번호가 일치하지 않습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "❌ Error : 이메일 · 비밀번호 조건 불만족 시 ", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/login")
    @BindingCheck
    ResponseEntity<Response<UserLoginResponse>> login(@RequestBody @Validated UserLoginRequest requestDto, BindingResult br);
}
