#
# build image
#

FROM openjdk:11-jdk as build
WORKDIR /workspace
COPY . /workspace
RUN sh gradlew build
RUN java -Djarmode=layertools -jar build/libs/moany-SNAPSHOT.jar extract

#
# deployment image
#

FROM openjdk:11-jre
WORKDIR /opt/software/moany
COPY --from=build /workspace/dependencies/ .
COPY --from=build /workspace/snapshot-dependencies/ .
COPY --from=build /workspace/resources/ .
COPY --from=build /workspace/application/ .
RUN mkdir -p config
VOLUME /opt/software/moany/config
ENV SPRING_PROFILES_ACTIVE="datasource" \
    DB_URL="jdbc:h2:mem:moany" \
    DB_USER="sa" \
    DB_PASSWORD="password" \
    DB_PLATFORM="h2"
EXPOSE 9080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

