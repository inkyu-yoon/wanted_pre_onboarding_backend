FROM gradle:7.4-jdk17-alpine as builder
WORKDIR /build

COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

COPY . /build
RUN gradle build -x test --parallel

# APP
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /build/build/libs/wanted-0.0.1-SNAPSHOT.jar .

EXPOSE 8081

# root 대신 nobody 권한으로 실행
USER nobody
ENTRYPOINT [                                                \
   "java",                                                 \
   "-jar",                                                 \
   "-Djava.security.egd=file:/dev/./urandom",              \
   "-Dsun.net.inetaddr.ttl=0",                             \
   "wanted-0.0.1-SNAPSHOT.jar"              \
]
