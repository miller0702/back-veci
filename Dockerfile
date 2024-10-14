FROM oraclelinux:8-slim AS build

RUN dnf install -y oracle-release-el8 && \
    dnf install -y oracle-java-23 && \
    dnf clean all

WORKDIR /app

RUN ./gradlew clean build --no-daemon

FROM oraclelinux:8-slim

RUN dnf install -y oracle-release-el8 && \
    dnf install -y oracle-java-23 && \
    dnf clean all

WORKDIR /app

COPY build/libs/veci-back-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar"]
