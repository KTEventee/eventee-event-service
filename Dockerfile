# --- build stage: jar 빌드 ---
FROM gradle:8.10-jdk17 AS build
WORKDIR /workspace

# Gradle 캐시 활용하려면 필요시 settings/gradle 파일 부분적으로 복사 가능
COPY . .
RUN ./gradlew bootJar --no-daemon

# --- run stage: 빌드된 jar 실행 ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# build 단계에서 만들어진 bootJar를 가져옴
COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
