FROM gradle:9-jdk17-alpine as build

WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM openjdk:17-ea-jdk-alpine3.14

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/notificacao.jar

EXPOSE 8084

CMD ["java", "-jar", "/app/usuario.jar"]