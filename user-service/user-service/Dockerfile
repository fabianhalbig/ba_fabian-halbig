FROM openjdk:11
ARG JAR_FILE=target/user-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} service-registry.jar
ENTRYPOINT ["java","-jar","/service-registry.jar"]
EXPOSE 9070
