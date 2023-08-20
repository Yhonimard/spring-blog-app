FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/spring-boot-blog-app.jar .

ENV DB_HOST=value

CMD ["java", "-jar", "spring-boot-blog-app.jar"]