#
# build image
#

FROM openjdk:11-jdk as build
WORKDIR /workspace
COPY gradlew ./
COPY gradle/ ./gradle/
RUN sh gradlew projects

COPY *.gradle *.env ./
COPY src/ ./src/
COPY .git/ ./.git/
RUN sh gradlew build
RUN java -Djarmode=layertools -jar build/libs/moany-SNAPSHOT.jar extract
RUN ls -al

#
# deployment image
#

FROM openjdk:11-jre
WORKDIR /opt/software/moany
COPY --from=build /workspace/dependencies/ ./
COPY --from=build /workspace/spring-boot-loader/ ./
COPY --from=build /workspace/snapshot-dependencies/ ./
COPY --from=build /workspace/application/ ./
RUN mkdir -p config
VOLUME /opt/software/moany/config
RUN ls -al
ENV SPRING_PROFILES_ACTIVE=datasource
ENV DB_URL=jdbc:h2:mem:moany
ENV DB_USER=sa
ENV DB_PASSWORD=password
ENV DB_PLATFORM=h2
EXPOSE 9080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

