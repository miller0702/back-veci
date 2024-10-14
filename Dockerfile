# Etapa 1: Construcción usando Oracle Linux con instalación de JDK 23
FROM oraclelinux:8-slim AS build

# Instalar microdnf, ya que dnf no está disponible en la imagen slim
RUN microdnf install -y oracle-release-el8 && \
    microdnf install -y oracle-java-23 && \
    microdnf clean all

# Establecer directorio de trabajo en el contenedor
WORKDIR /app

# Copiar los archivos del proyecto al contenedor
COPY . .

# Ejecutar la construcción de la aplicación
RUN ./gradlew clean build --no-daemon

# Etapa 2: Ejecutar la aplicación con Oracle JDK 23
FROM oraclelinux:8-slim

# Instalar microdnf para manejar la instalación de Java en esta etapa también
RUN microdnf install -y oracle-release-el8 && \
    microdnf install -y oracle-java-23 && \
    microdnf clean all

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR generado desde la etapa anterior
COPY --from=build /app/build/libs/veci-back-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto 5000 para la aplicación Spring Boot
EXPOSE 5000

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
