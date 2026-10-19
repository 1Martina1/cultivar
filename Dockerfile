# Parti da un'immagine base che ha già Java 21 dentro
FROM eclipse-temurin:21-jre

# Copia dentro il mio jar e chiamalo app.jar
COPY target/cultivar-0.0.1-SNAPSHOT.jar app.jar

# L'app ascolta sulla porta 8080
EXPOSE 8080

# Quando il container parte, esegui: java -jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]