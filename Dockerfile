FROM eclipse-temurin:21-jdk

ARG GRADLE_VERSION=8.10

WORKDIR .

COPY ./ .

RUN ./gradlew installDist

EXPOSE 8080

CMD ./build/install/app/bin/app