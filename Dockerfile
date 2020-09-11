#
# build image
#

FROM openjdk:11-jdk as build
WORKDIR /workspace

# copy gradle wrapper assets to create layer that should only change on gradle version/wrapper change 
COPY gradlew ./
COPY gradle/ ./gradle/
RUN sh gradlew projects

# copy all project assets for application build/test
COPY ./src/ ./src/
COPY ./*.gradle ./
COPY ./*.env ./
COPY ./*.properties ./
RUN sh gradlew build

# extract spring boot layered jars for deployment image
ARG version=SNAPSHOT
RUN source ./gradle.properties && cd build && java -Djarmode=layertools -jar libs/moany-${version}.jar extract

#
# deployment image
#

FROM openjdk:11-jre
WORKDIR /opt/software/moany

# copy layered jars
COPY --from=build /workspace/build/dependencies/ ./
COPY --from=build /workspace/build/spring-boot-loader/ ./
COPY --from=build /workspace/build/snapshot-dependencies/ ./
COPY --from=build /workspace/build/application/ ./

RUN mkdir -p config
VOLUME /opt/software/moany/config
ENV SPRING_PROFILES_ACTIVE=datasource
ENV DB_URL=jdbc:h2:mem:moany
ENV DB_USER=sa
ENV DB_PASSWORD=password
ENV DB_PLATFORM=h2
EXPOSE 9080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

