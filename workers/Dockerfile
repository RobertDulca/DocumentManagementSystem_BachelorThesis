# BUILDER Stage - Build the project with Maven
FROM maven:3.9.9-amazoncorretto-21-alpine AS build
ENV HOME=/usr/app
WORKDIR $HOME

# Copy project files and build
COPY pom.xml $HOME
RUN mvn verify --fail-never
COPY . $HOME
RUN mvn package -e

# RUNNER Stage - Create a lightweight container to run the built application
FROM openjdk:24-jdk-slim AS runner

# Download English language data for Tesseract
RUN mkdir -p /app/tessdata

# Install Tesseract OCR and dependencies
RUN apt-get update \
    && apt-get install -y tesseract-ocr wget curl \
    && rm -rf /var/lib/apt/lists/*

RUN wget -O /app/tessdata/eng.traineddata https://github.com/tesseract-ocr/tessdata/raw/refs/heads/main/eng.traineddata

# Set the TESSDATA_PREFIX environment variable for Tesseract
ENV TESSDATA_PREFIX=/app/tessdata

# Verify Tesseract installation
RUN tesseract --version && tesseract --list-langs

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=build /usr/app/target/*.jar ocrService.jar

# Expose the application port
EXPOSE 8082

# Start the worker service
ENTRYPOINT ["java", "-jar", "ocrService.jar"]
