FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /workspace/app
COPY . .

RUN chmod +x ./gradlew


RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
