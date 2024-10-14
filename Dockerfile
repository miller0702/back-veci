FROM openjdk:23-slim AS build

# Establecer directorio de trabajo en el contenedor
WORKDIR /app

# Copiar los archivos del proyecto al contenedor
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
