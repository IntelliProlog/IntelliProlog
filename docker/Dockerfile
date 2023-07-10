# This file is a dockerfile to build a preconfigured container for the testing pipeline
# It is based on the official gradle image and installs the required tools and dependencies
# to run the tests

# Use the official gradle image
#TO UPDATE according to the gradle version used in the project
FROM gradle:8.2.0-jdk17

# Gradle install the dependencies
COPY ../build.gradle.kts ../gradle.properties ./
RUN gradle --refresh-dependencies
