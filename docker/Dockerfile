#adoptopenjdk/maven-openjdk11 
#FROM maven:3.6.3-openjdk-11-slim
FROM maven:3-amazoncorretto-11 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

FROM amazoncorretto:11.0.10-alpine

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/prototype-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "prototype-0.0.1-SNAPSHOT.jar"]
