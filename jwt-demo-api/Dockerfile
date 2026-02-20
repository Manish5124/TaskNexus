# -------- Stage 1: Build --------
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy only Gradle files first (for caching dependencies)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Now copy source code
COPY src src

RUN ./gradlew bootJar --no-daemon

# -------- Stage 2: Runtime --------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]