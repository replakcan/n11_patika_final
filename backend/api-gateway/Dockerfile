FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace/backend/api-gateway

COPY backend/api-gateway/pom.xml pom.xml
COPY backend/api-gateway/src src

RUN mvn -B -DskipTests package \
    && cp "$(find target -maxdepth 1 -name '*.jar' ! -name '*.jar.original' | head -n 1)" /tmp/app.jar

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /tmp/app.jar /app/app.jar

EXPOSE 8079

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:-} -jar /app/app.jar"]
