# Stage 1: Build
FROM gradle:8.12-jdk21-alpine AS builder

WORKDIR /app

# Cache Gradle dependencies - only re-downloaded when build files change
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY libs ./libs
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet

# Build application
COPY src ./src
COPY config ./config
RUN ./gradlew bootJar --no-daemon --quiet -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install DejaVu fonts for full Unicode/Polish characters support in PDF generation
# font-dejavu is the correct package name in Alpine 3.16+
RUN apk add --no-cache font-dejavu || apk add --no-cache ttf-dejavu || true

# Non-root user for security
RUN addgroup -S teachly && adduser -S teachly -G teachly && mkdir -p /app/logs /app/uploads && chown teachly:teachly /app/logs /app/uploads
USER teachly

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
