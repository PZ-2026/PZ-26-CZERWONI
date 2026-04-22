# Stage 1: Build
FROM gradle:8.12-jdk21-alpine AS builder

WORKDIR /app

# Cache Gradle dependencies - only re-downloaded when build files change
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet

# Build application
COPY src ./src
COPY config ./config
RUN ./gradlew bootJar --no-daemon --quiet -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S teachly && adduser -S teachly -G teachly
USER teachly

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
