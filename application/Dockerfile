# Базовый образ с Java 17
FROM eclipse-temurin:17-jdk

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем jar-файл (будет сделан mvn package перед этим)
COPY target/*.jar app.jar

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
