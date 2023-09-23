FROM openjdk:17-jdk-slim-buster

# Create tmp directory
WORKDIR /tmp

# Copy source to temp directory
COPY . .

# Clean output directory
RUN mvn clean

# Build app
RUN mvn package

# Copy binary to work directory
COPY target/staroverlay-eventsub-jar-with-dependencies.jar /app/app.jar

# Set work directory
WORKDIR /app

# Run app
CMD ["java", "-jar", "app.jar"]
