
#
# build image
#

FROM docker.io/library/eclipse-temurin:21 AS build
WORKDIR /workspace

# copy gradle wrapper assets to create layer that should only change on gradle version/wrapper change 
COPY gradlew ./
COPY gradle/ ./gradle/
# RUN sh gradlew init # init is failing since Gradle 8.x?

# copy all project assets for application build/test
COPY ./src/ ./src/
COPY ./*.gradle ./*.env ./

ARG BUILD_VERSION=SNAPSHOT
RUN echo "version=${BUILD_VERSION}" > gradle.properties
RUN sh /workspace/gradlew build
RUN ls -lR build/reports || true

# extract spring boot layered jars for deployment image
#WORKDIR build
#RUN java -Djarmode=layertools -jar libs/moany-$BUILD_VERSION.jar extract

#
# deployment image
#

FROM docker.io/library/eclipse-temurin:21-jre
WORKDIR /opt/software

# copy layered jars
#COPY --from=build /workspace/build/dependencies/ ./
#COPY --from=build /workspace/build/spring-boot-loader/ ./
#COPY --from=build /workspace/build/snapshot-dependencies/ ./
#COPY --from=build /workspace/build/application/ ./

#RUN mkdir -p config
#VOLUME /opt/software/moany/config
ENV SPRING_PROFILES_ACTIVE=datasource
ENV DB_URL=jdbc:h2:mem:moany
ENV DB_USER=sa
ENV DB_PASSWORD=password
HEALTHCHECK --interval=30s --timeout=30s --start-period=120s --retries=3 CMD curl -f http://localhost:9080/status || exit 1
EXPOSE 9080
#ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
ARG BUILD_VERSION=SNAPSHOT
COPY --from=build /workspace/build/libs/moany-${BUILD_VERSION}.jar /opt/software/moany.jar
CMD ["java","-jar","/opt/software/moany.jar"]

