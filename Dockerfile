FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src ./src

# Compile, run tests and create the executable JAR.
# If any test fails, the Docker build and deployment stop.
RUN mvn -B clean verify


FROM eclipse-temurin:21-jre

WORKDIR /app

# A fixed UID/GID makes persistent-storage permissions predictable.
RUN groupadd --gid 10001 spring \
    && useradd \
        --uid 10001 \
        --gid spring \
        --no-create-home \
        --shell /usr/sbin/nologin \
        spring \
    && mkdir -p /app/uploads \
    && chown -R 10001:10001 /app

COPY --from=build \
    --chown=10001:10001 \
    /app/target/*.jar \
    /app/app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]