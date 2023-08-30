FROM bellsoft/liberica-runtime-container:jre-17-slim-musl
ARG SOURCE_DIR
WORKDIR /home/app
COPY ./${SOURCE_DIR}/target/*.jar ./application.jar
EXPOSE 8080
CMD ["java", "-jar", "application.jar"]
