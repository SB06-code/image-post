# --- Stage 1: Build Frontend (React) ---
FROM node:20-alpine AS builder-fe

WORKDIR /app-fe

# FE 의존성 설치
COPY frontend-react-src/package.json frontend-react-src/package-lock.json ./
RUN npm install

# FE 소스 복사 및 빌드
COPY frontend-react-src/ ./
RUN npm run build


# --- Stage 2: Build Backend (Spring Boot) ---
FROM gradle:8.14.3-jdk17 AS builder-be

WORKDIR /app-be

# BE 의존성 설치
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle
RUN ./gradlew dependencies

# BE 소스 복사
COPY src ./src

# (★) Stage 1(FE)의 빌드 결과물을 BE의 정적 리소스 경로로 복사
COPY --from=builder-fe /app-fe/build ./src/main/resources/static/app

# BE 빌드 (FE 파일이 포함된 JAR 생성)
RUN ./gradlew bootJar


# --- Stage 3: Final Runtime Image ---
FROM amazoncorretto:17

WORKDIR /app

# (★) Stage 2(BE)에서 빌드된 JAR 파일을 'app.jar'로 복사
COPY --from=builder-be /app-be/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]