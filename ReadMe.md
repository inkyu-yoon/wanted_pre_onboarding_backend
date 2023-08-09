# 원티드 프리온보딩 백엔드 인턴십 사전 과제

## 과제 제출 필수 사항

### 지원자의 성명

윤인규

[Controller & Service 테스트 코드 작성](https://wanted-yooninkyu-test-code.netlify.app/)

![img.png](img.png)

Jacoco 라이브러리를 통해 Controller 와 Service 테스트 코드 커버리지를 측정하여 100% 달성하였습니다.

### 애플리케이션의 실행 방법

1. [application.yml](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/resources/application.yml) 에 mysql username과 password를 입력

2. jwt 생성을 위한 secret Key는 보안상 적절한 키워드로 변경

3. mysql 스키마 `wanted-db` 추가 

4. git clone 혹은 zip 파일로 받아 intellij 환경에서 실행

### 데이터 베이스 테이블 구조(ERD)

![erd](wanted_erd.png)

### 구현한 API의 동작을 촬영한 데모 영상 링크

[시연 영상 유투브 링크](https://www.youtube.com/watch?v=OE6it1xphcI)

### 구현 방법 및 이유에 대한 간략한 설명

#### 1. Request Dto 유효성 검증

회원가입과 로그인 시, `email` 의 경우 `@` 문자가 꼭 포함되어야 하고 비밀번호는 반드시 8자 이상이어야 합니다.

서비스 로직 내에서, 정규식이나 입력 받은 비밀번호의 길이를 체크하는 방식으로 구현이 가능하지만

간결한 코드 작성을 위해 `spring-validation` 라이브러리를 사용하여 유효성을 검증하였습니다.

[회원 가입 RequestDto](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/domain/user/dto/UserCreateRequest.java)  
[로그인 RequestDto](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/domain/user/dto/UserLoginRequest.java)  

`@Email` 과 `@Length` 어노테이션을 이용하여, 요구사항에 맞게 유효성을 검증하였습니다.

또한, 이렇게 유효성을 검증하여 예외를 처리하는 부분을 커스텀 어노테이션을 정의하여 컨트롤러 가독성을 높이고자 하였습니다.

[커스텀 어노테이션 @BindingCheck](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/global/annotation/BindingCheck.java)  
[Advisor 클래스](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/global/aop/BindingCheckAop.java)  

**기존 코드**

```java
@PostMapping
public ResponseEntity<Response<UserCreateResponse>> create(@RequestBody @Validated UserCreateRequest requestDto, BindingResult br) {

        if (br.hasErrors()) {
        throw new BindingException(br.getFieldError().getDefaultMessage());
        }

        UserCreateResponse response = userService.createUser(requestDto);

        return ResponseEntity.ok(Response.success(response));
}
```

**개선 후 코드**

```java
// 개선 후 코드
@PostMapping
@BindingCheck
public ResponseEntity<Response<UserCreateResponse>> create(@RequestBody @Validated UserCreateRequest requestDto, BindingResult br) {

        UserCreateResponse response = userService.createUser(requestDto);

        return ResponseEntity.ok(Response.success(response));
}
```

#### 2. 비밀번호 암호화

요구사항에 따라, 회원 가입 시 요청한 비밀번호를 암호화 하여 DB에 저장해야합니다.

Spring-security 라이브러리에서 제공하는 `BcryptPasswordEncoder` 클래스를 활용하여 암호화하였습니다.

`BcryptPasswordEncoder` 의 경우 단순히 입력을 1회 해시하는 것이 아니라, 랜덤의 salt를 부여하여 여러번 해싱 과정을 거치기 때문에

보안상 안전하다는 장점이 있습니다.

또한, `matches()` 메서드를 활용하여 암호화된 비밀번호와 raw 한 비밀번호가 일치하는지 확인할 수 있습니다.

[EncryptConfig.java](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/global/config/EncryptConfig.java) 와 같이 정의한 뒤, 

`BcryptPasswordEncoder` 빈을 주입받아 `encode()` 메서드로 암호화를 하면 되고 `matches(비밀번호,암호화 된 비밀번호)` 로 비밀번호를 검증하면 됩니다.

#### 3. 예외 처리

`AppException` 이라는 커스텀 예외를 정의한 뒤, 예외 처리시 `AppException` 을 throw 하도록 구현하였습니다.

`@RestControllerAdvice` 와 `@ExceptionHandler` 를 사용하여 `Controller` 에서 발생하는 `AppException`을 핸들링하여

사용자에게 에러 원인과 메세지를 전달하도록 구현하였습니다.

에러 메세지와 코드는 Enum 타입으로 관리하였습니다.

[AppException.java](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/global/exception/AppException.java)
[ErrorCode.java](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/global/exception/ErrorCode.java)
[ExceptionManager.java](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/global/exception/ExceptionManager.java)



#### 4. 인증과 인가

[JwtAuthenticationFilter ](https://github.com/inkyu-yoon/wanted_pre_onboarding_backend/blob/main/src/main/java/com/wanted/global/filter/JwtAuthenticationFilter.java) 를 정의하여 사용자가 전달하는 jwt의 유효성을 판단합니다.

토큰이 존재하지 않거나 만료된 경우를 제외하고 유효한 jwt라면 인가 과정을 거쳐 `SecurityContextHolder` 에 인증 정보를 입력할 수 있습니다.

따라서, 사용자 인증이 필요한 api에서 유효한 jwt 임을 확인하고 Authentication 객체를 활용해 요청했던 사용자의 정보를 활용해 부가적인 기능을 수행할 수 있습니다.

```java
@DeleteMapping("/{postId}")
public ResponseEntity<Response<PostDeleteResponse>> delete(Authentication authentication, @PathVariable(name = "postId") Long postId) {

PostDeleteResponse response = postService.deletePost(postId, authentication.getName());

return ResponseEntity.ok(Response.success(response));
}
```

예시로 위와 같이, 게시글을 삭제하는 api에서 게시글 삭제를 요청한 사용자의 정보를 `authentication.getName()` 를 통해 얻을 수 있고

이 정보로, Service 코드에서 게시글 작성자와 삭제 요청자가 일치하는지 확인할 수 있게 됩니다.


### API 명세(request/response 포함)


#### 1. 회원가입 (POST) : `api/v1/users`

**Request Body**

```
{
  "email": "email@email.com",
  "password": "12345678"
}
```

**Response Body**

```
{
    "message": "SUCCESS",
    "result": {
        "userId": 1,
        "email": "email@email.com"
    }
}
```

- 이메일과 비밀번호로 회원가입 할 수 있는 엔드포인트입니다.
- 이미 등록된 이메일로 중복 가입 요청 시 예외 처리 됩니다.
- 이메일에는 `@` 가 반드시 포함되어있어야 합니다.
- 비밀번호는 반드시 8자 이상 되어야 합니다.
- 비밀번호는 DB에 암호화되어 저장됩니다.

**Error Response**

```
{
    "message": "ERROR",
    "result": "이미 존재하는 이메일 입니다."
}
```

```
{
    "message": "ERROR",
    "result": "올바른 이메일 형식이 아닙니다. '@' 를 포함시켜주세요."
}
```

```
{
    "message": "ERROR",
    "result": "비밀번호는 최소 8자 이상입니다."
}
```

<br>

#### 2. 로그인 (POST) : `api/v1/users/login`

**Request Body**

```
{
  "email": "email@email.com",
  "password": "12345678"
}
```

**Response Body**

```
{
    "message": "SUCCESS",
    "result": {
        "userId": 1,
        "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWFpbEBlbWFpbC5jb20iLCJpYXQiOjE2OTE1NjAxMjUsImV4cCI6MTY5MTU2MzcyNX0.NYqFUMHrSd_LPuSA6I80keJhQgvny6_iOdZ0mBNtckc"
    }
}
```

- 회원가입 된, 이메일과 비밀번호로 로그인 할 수 있는 엔드포인트입니다.
- 이메일에는 `@` 가 반드시 포함되어있어야 합니다.
- 비밀번호는 반드시 8자 이상 되어야 합니다.
- 이메일로 가입된 회원이 없는 경우 예외 처리 됩니다.
- 비밀번호가 일치하지 않을 시 예외 처리 됩니다.

**Error Response**

```
{
    "message": "ERROR",
    "result": "올바른 이메일 형식이 아닙니다. '@' 를 포함시켜주세요."
}
```

```
{
    "message": "ERROR",
    "result": "비밀번호는 최소 8자 이상입니다."
}
```

```
{
    "message": "ERROR",
    "result": "가입된 회원을 찾을 수 없습니다."
}
```

```
{
    "message": "ERROR",
    "result": "비밀번호가 일치하지 않습니다."
}
```

<br>


#### 3. 게시글 생성 (POST) : `api/v1/posts`

**Request Body**

```
{
  "title": "title",
  "body": "body"
}
```

**Response Body**

```
{
    "message": "SUCCESS",
    "result": {
        "userId": 1,
        "postId": 1,
        "title": "title",
        "body": "body"
    }
}
```

- 제목과 내용을 전달하여 게시글을 작성하는 엔드포인트 입니다.
- Http Authorization 헤더에 로그인 후 받은 JWT 를 전달해야합니다.
- 가입된 회원이 아닌 경우 예외 처리 됩니다.

**Error Response**

```
{
    "message": "ERROR",
    "result": "유효한 토큰이 아닙니다."
}
```

```
{
    "message": "ERROR",
    "result": "가입된 회원을 찾을 수 없습니다."
}
```

<br>

#### 4. 게시글 목록 조회 (GET) : `api/v1/posts`


**Response Body**

```
{
    "message": "SUCCESS",
    "result": {
        "content": [
            {
                "postId": 1,
                "author": "email@email.com",
                "title": "title",
                "body": "body"
            }
        ],
        "pageable": {
            "sort": {
                "empty": true,
                "sorted": false,
                "unsorted": true
            },
            "offset": 0,
            "pageSize": 20,
            "pageNumber": 0,
            "paged": true,
            "unpaged": false
        },
        "totalPages": 1,
        "totalElements": 1,
        "last": true,
        "size": 20,
        "number": 0,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "first": true,
        "numberOfElements": 1,
        "empty": false
    }
}
```


<br>


#### 5. 게시글 단건 조회 (GET) : `api/v1/posts/{postId}`


**Response Body**

```
{
    "message": "SUCCESS",
    "result": {
        "postId": 1,
        "author": "email@email.com",
        "title": "title",
        "body": "body"
    }
}
```

- postId를 전달하여, postId에 해당하는 게시글을 조회하는 엔드포인트 입니다.
- postId에 해당하는 게시글이 존재하지 않을 시, 예외 처리 됩니다.

**Error Response**

```
{
    "message": "ERROR",
    "result": "게시글을 찾을 수 없습니다."
}
```

<br>


#### 6. 게시글 수정 (PUT) : `api/v1/posts/{postId}`

**Request Body**

```
{
  "title": "update title",
  "body": "update body"
}
```


**Response Body**

```
{
    "message": "SUCCESS",
    "result": {
        "postId": 1,
        "title": "update title",
        "body": "update body"
    }
}
```

- postId와 수정할 제목과 내용을 전달하여, postId에 해당하는 게시글을 수정하는 엔드포인트 입니다.
- Http Authorization 헤더에 로그인 후 받은 JWT 를 전달해야합니다.
- 가입된 회원이 아닌 경우 예외 처리 됩니다.
- postId에 해당하는 게시글이 존재하지 않을 시, 예외 처리 됩니다.
- 게시글 작성자가 본인이 아닌 경우 예외 처리 됩니다.

**Error Response**

```
{
    "message": "ERROR",
    "result": "유효한 토큰이 아닙니다."
}
```

```
{
    "message": "ERROR",
    "result": "가입된 회원을 찾을 수 없습니다."
}
```

```
{
    "message": "ERROR",
    "result": "게시글을 찾을 수 없습니다."
}
```

```
{
    "message": "ERROR",
    "result": "작성자 본인만 요청할 수 있습니다."
}
```

<br>


#### 7. 게시글 삭제 (DELETE) : `api/v1/posts/{postId}`


**Response Body**

```
{
    "message": "SUCCESS",
    "result": {
        "postId": 1,
        "title": "title",
        "body": "body"
    }
}
```

- postId를 전달하여, postId에 해당하는 게시글을 삭제하는 엔드포인트 입니다.
- Http Authorization 헤더에 로그인 후 받은 JWT 를 전달해야합니다.
- 가입된 회원이 아닌 경우 예외 처리 됩니다.
- postId에 해당하는 게시글이 존재하지 않을 시, 예외 처리 됩니다.
- 게시글 작성자가 본인이 아닌 경우 예외 처리 됩니다.

**Error Response**

```
{
    "message": "ERROR",
    "result": "유효한 토큰이 아닙니다."
}
```

```
{
    "message": "ERROR",
    "result": "가입된 회원을 찾을 수 없습니다."
}
```

```
{
    "message": "ERROR",
    "result": "게시글을 찾을 수 없습니다."
}
```

```
{
    "message": "ERROR",
    "result": "작성자 본인만 요청할 수 있습니다."
}
```

<br>
