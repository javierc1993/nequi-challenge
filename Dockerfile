# Etapa 1: Construcción (Builder) - Usa una imagen con el JDK completo para compilar
FROM eclipse-temurin:17-jdk-jammy as builder

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /workspace/app

RUN apt-get update && apt-get install -y git

RUN git clone https://gitlab.com/javier.cuchumbe/nequi-challenge.git .

RUN chmod +x ./gradlew

RUN ./gradlew bootJar

# Etapa 2: Ejecución (Runtime) - Usa una imagen más ligera solo con el JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia solo el JAR construido de la etapa anterior
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

# Expone el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación cuando el contenedor inicie
ENTRYPOINT ["java", "-jar", "app.jar"]