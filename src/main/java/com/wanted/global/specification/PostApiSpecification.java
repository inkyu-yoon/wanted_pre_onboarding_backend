package com.wanted.global.specification;

import com.wanted.domain.post.dto.*;
import com.wanted.global.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "게시글 정보 관련 API")
public interface PostApiSpecification {


    @Operation(summary = "게시글 작성", description = "<strong>🔑JWT 필요</strong><br>💡게시글을 작성합니다.<br>🚨가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{  \"userId\": 1, \"postId\": 1, \"title\": \"title\", \"body\": \"body\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (유효하지 않은 JWT 전달 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"유효한 토큰이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping
    ResponseEntity<Response<PostCreateResponse>> create(@RequestBody PostCreateRequest requestDto, Authentication authentication);

    @Operation(summary = "게시글 단건 조회", description = "💡게시글을 단건 조회합니다.<br>🚨게시글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1,\"author\":\"author\",\"title\":\"title\",\"body\":\"body\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{postId}")
    ResponseEntity<Response<PostGetResponse>> get(@PathVariable(name = "postId") Long postId);

    @Operation(summary = "게시글 조회", description = "💡모든 게시글을 페이지로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"content\":[{\"postId\":1,\"author\":\"author\",\"title\":\"title\",\"body\":\"body\"}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":20,\"unpaged\":false,\"paged\":true},\"totalPages\":1,\"totalElements\":2,\"last\":true,\"size\":20,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"first\":true,\"empty\":false}}")}, schema = @Schema(implementation = Response.class))),
    })
    @GetMapping
    ResponseEntity<Response<Page<PostGetResponse>>> getPage(@Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "게시글 수정", description = "<strong>🔑JWT 필요</strong><br>💡게시글을 수정합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 본인 게시글 수정 요청이 아닐시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1, \"title\": \"update title\", \"body\": \"update body\" }}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (유효하지 않은 JWT 전달 시 · 작성자 본인이 요청하지 않는 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PutMapping("/{postId}")
    ResponseEntity<Response<PostUpdateResponse>> update(@RequestBody PostUpdateRequest requestDto, Authentication authentication, @PathVariable(name = "postId") Long postId);

    @Operation(summary = "게시글 삭제", description = "<strong>🔑JWT 필요</strong><br>💡게시글을 삭제합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 본인 게시글 삭제 요청이 아닐시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1, \"title\": \"title\", \"body\": \"body\" }}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (유효하지 않은 JWT 전달 시 · 작성자 본인이 요청하지 않는 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"에러 메세지\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/{postId}")
    ResponseEntity<Response<PostDeleteResponse>> delete(Authentication authentication, @PathVariable(name = "postId") Long postId);

}
