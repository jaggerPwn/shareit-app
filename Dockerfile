FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar shareit.jar
ENTRYPOINT ["java","-jar","/shareit.jar"]