# Etapa 1: Construcción usando OpenJDK 23
FROM openjdk:23-slim AS build
LABEL authors="Miller Alvarez"
# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo de permisos para Gradle y la carpeta gradle/wrapper al contenedor
COPY gradlew ./
COPY gradle ./gradle

# Dar permisos de ejecución a gradlew
RUN chmod +x ./gradlew

# Copiar el resto de los archivos del proyecto
COPY . .

# Ejecutar la construcción de la aplicación
RUN ./gradlew clean build --no-daemon

# Etapa 2: Ejecutar la aplicación con OpenJDK 23
FROM openjdk:23-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR generado desde la etapa anterior
COPY --from=build /app/build/libs/veci-back-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto 5000 para la aplicación Spring Boot
EXPOSE 5000

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]