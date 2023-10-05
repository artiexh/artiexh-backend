FROM bellsoft/liberica-runtime-container:jdk-all-17-slim-musl AS layers
ARG SOURCE_DIR
WORKDIR /home/app
COPY ./${SOURCE_DIR}/target/*.jar ./application.jar
RUN jar xf application.jar
RUN jdeps --ignore-missing-deps -q \
    --recursive \
    --multi-release 17 \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*' \
    application.jar > deps.info
RUN jlink --add-modules $(cat deps.info) \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output artiexh-jre
RUN java -Djarmode=layertools -jar application.jar extract

FROM bellsoft/alpaquita-linux-base:stream-musl
RUN apk del --no-cache apk-tools
ENV JAVA_HOME /user/java/jre
COPY --from=layers /home/app/artiexh-jre $JAVA_HOME
WORKDIR /home/app
COPY --from=layers /home/app/dependencies/ ./
COPY --from=layers /home/app/spring-boot-loader/ ./
COPY --from=layers /home/app/snapshot-dependencies/ ./
COPY --from=layers /home/app/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
EXPOSE 8080
