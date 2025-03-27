FROM openjdk:21-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY target/Security-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]