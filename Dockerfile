# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle

RUN chmod +x gradlew \
    && ./gradlew --no-daemon dependencies >/dev/null

COPY src ./src

# Build the executable jar and run the context tests inside the image build.
RUN ./gradlew --no-daemon clean build

FROM eclipse-temurin:25-jre-alpine

RUN apk add --no-cache curl \
    && addgroup -S luxe \
    && adduser -S luxe -G luxe

WORKDIR /app

COPY --from=build --chown=luxe:luxe /workspace/build/libs/*.jar /app/app.jar

USER luxe

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

EXPOSE 16801

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=12 \
    CMD curl --fail --silent http://127.0.0.1:16801/actuator/health >/dev/null || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
