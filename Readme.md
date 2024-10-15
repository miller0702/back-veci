# Veci App - Backend de Recargas con Spring Boot

## Tabla de Contenidos

- [Introducción](#introducción)
- [Documentación](#documentación)
- [Características](#características)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Instalación](#instalación)
- [Uso](#uso)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Variables de Entorno](#configuración)
- [Scripts Disponibles](#scripts-disponibles)

---

## Introducción

**Veci App** es una aplicación que permite a los usuarios realizar recargas móviles y de servicios. Este repositorio contiene el backend de la aplicación, desarrollado con **Spring Boot**, que expone los servicios API necesarios para la interacción del frontend. El backend maneja la autenticación, la lógica de las recargas y la integración con proveedores externos de servicios.

## Documentación

Podrás ver todas las documentación realizada en postman sobre cada uno de los endpoints

- [Postman](https://documenter.getpostman.com/view/20358776/2sAXxTdXBQ)

## Características

- API RESTful para gestionar usuarios y transacciones de recarga
- Autenticación y autorización mediante tokens JWT
- Integración con MongoDB para el almacenamiento de usuarios y transacciones
- Gestión de proveedores de recarga
- Manejo de errores y validación de datos
- Historial de recargas y transacciones de usuarios

## Tecnologías Utilizadas

- **Backend:**
    - Spring Boot (Java)
    - MongoDB como base de datos
    - Gradle como herramienta de construcción

- **Infraestructura:**
    - MongoDB Atlas para la base de datos en la nube
    - Docker para la contenedorización de la aplicación

- **Servidor Web:**

  - La aplicación se encuentra desplegada y en completo funcionamiento en render
    [Render](https://render.com)

  - Link del sitio VECI APP
    [Veci App](https://back-veci.onrender.com)

  * Credenciales para probar los endpoints:
    user: ```bash user0147```
    password: ```bash #3Q34Sh0NlDS```

## Instalación

Para ejecutar el proyecto localmente, sigue estos pasos:

### Prerrequisitos

Asegúrate de tener instalado lo siguiente:

- JDK 23
- Gradle 8.x
- MongoDB (local o remoto)
- Docker (opcional, para contenedorización)

### Clonar el Repositorio

```bash
git clone https://github.com/miller0702/back-veci.git
cd back-veci
```

### Configuración

En el archivo `application.properties` configura tus variables de entorno para las credenciales de la base de datos y otros parámetros:

```bash
spring.data.mongodb.uri=mongodb+srv://<usuario>:<password>@cluster.mongodb.net/<nombre-db>
server.port=5000
spring.main.allow-bean-definition-overriding=true
puntored.api.base-url=https://us-central1-puntored-dev.cloudfunctions.net/technicalTest-developer/api
puntored.x.api.key=mtrQF6Q11eosqyQnkMY0JGFbGqcxVg5icvfVnX1ifIyWDvwGApJ8WUM8nHVrdSkN
```

### Construir y Ejecutar

Para compilar y ejecutar la aplicación, utiliza los siguientes comandos:

```bash
./gradlew build
./gradlew bootRun
```

Esto iniciará el backend en `http://localhost:5000`.

## Uso

Una vez que el backend esté en funcionamiento, puedes interactuar con las siguientes rutas principales:

- **`/auth`** - Inicia sesión y obtiene un Bearer token.
- **`/getSuppliers`** - Gestión de proveedores (registro, actualización).
- **`/buy`** - Gestión de recargas (registro).
- **`/transacciones`** - Endpoints para gestionar recargas de servicios y móviles.
- **`/transaccion/{id}/ticket`** - Endpoints para obtener el ticket de la recarga.

Puedes utilizar herramientas como Postman o Curl para realizar peticiones a la API.

## Estructura del Proyecto

```bash
RecargasApp-Backend/
├── src/
│   ├── main/
│   │   ├── java/veci.veciback/   # Paquete principal de la aplicación
│   │   │   ├── config/              # Configuraciones de cors y seguridad
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── model/               # Entidades de MongoDB y clases DTO
│   │   │   ├── repository/          # Repositorios de MongoDB
│   │   │   ├── service/             # Servicios para la lógica de negocio
│   │   └── resources/                    
│   │       ├── assets               # Archivos utilizados en el ticket
│   │       ├── application.yml      # Configuraciones de la aplicación
│   └── test/                        # Tests unitarios y de integración
├── build.gradle                     # Configuración de Gradle
├── .gitignore                       # Archivos ignorados por Git
├── README.md                        # Documentación del proyecto
└── ...
```

## Scripts Disponibles

- **`./gradlew bootRun`** - Ejecuta la aplicación en modo desarrollo.
- **`./gradlew build`** - Compila el proyecto y genera un archivo JAR listo para producción.
- **`./gradlew test`** - Ejecuta los tests unitarios y de integración.
