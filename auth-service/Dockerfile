FROM maven:3.8.5-openjdk-17
WORKDIR /auth-service
COPY target/auth-service-0.0.1-SNAPSHOT.jar
EXPOSE 8088
CMD ["java","-jar","app.jar","--spring.profiles.active=docker"]
