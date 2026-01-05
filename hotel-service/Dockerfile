FROM maven:3.8.5-openjdk-17
WORKDIR /hotel-service
COPY target/hotel-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8002
CMD ["java","-jar","app.jar","--spring.profiles.active=docker"]
