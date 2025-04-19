# Use Maven to build the project
FROM maven:3.9.9-amazoncorretto-21-alpine AS build
ENV HOME=/usr/app
WORKDIR $HOME
COPY pom.xml $HOME
RUN mvn verify --fail-never
COPY . $HOME
RUN mvn package -e

# Create the actual container to run the built project
FROM openjdk:21-jdk-slim

# Install curl
RUN apt-get update && apt-get install -y curl && apt-get clean

#Copy build to image
COPY --from=build /usr/app/target/openapi-spring-1.0.0.jar /app/runner.jar
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]
