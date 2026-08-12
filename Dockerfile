FROM eclipse-temurin:21-jdk-noble AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw --batch-mode dependency:go-offline
COPY src src
RUN ./mvnw --batch-mode -DskipTests package

FROM eclipse-temurin:21-jre-noble
RUN groupadd --system examflow && useradd --system --gid examflow --uid 10001 examflow
WORKDIR /app
COPY --from=build /workspace/target/examflow-*.jar app.jar
RUN mkdir data uploads && chown -R examflow:examflow /app
USER examflow
EXPOSE 7890
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
