
# Use the official maven/Java image to create a build artifact.
# This is based on Debian and sets the MAVEN_VERSION in the environment to your project's pom.xml
FROM maven:3.9.4-openjdk-17-slim as builder

# Copy src code to the container
WORKDIR /usr/src/app
COPY src ./src
COPY pom.xml ./

# Build a release artifact
RUN mvn clean package

# Use the official openjdk image for a base image.
# It's based on Debian and sets the JAVA_VERSION in the environment to your project's pom.xml
FROM openjdk:17-jdk-slim

WORKDIR /usr/app

# Copy the jar file built in the first stage
COPY --from=builder /usr/src/app/target/qosocial-api-0.0.1.jar ./qosocial-api-0.0.1.jar

# Set the spring profiles active environment variable
# spring will automatically grab this env and apply it, no need to manually inject into app.properties
ENV SPRING_PROFILES_ACTIVE=prod

# Your application's port number
EXPOSE 8080

# Run your jar file
ENTRYPOINT ["java","-jar","./qosocial-api-0.0.1.jar"]
