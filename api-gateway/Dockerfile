FROM openjdk:11
ARG JAR_FILE=target/api-gateway-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} service-registry.jar
ENTRYPOINT ["java","-jar","/service-registry.jar"]
EXPOSE 8080