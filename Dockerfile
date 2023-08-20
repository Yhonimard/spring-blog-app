FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/blog-app.jar .

CMD ["java", "-jar", "blog-app.jar"]