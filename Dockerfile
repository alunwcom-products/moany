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
COPY ./ ./
ENV NO_VERSION=true
RUN sh gradlew build

# extract spring boot layered jars for deployment image
RUN java -Djarmode=layertools -jar build/libs/moany.jar extract

#
# deployment image
#

FROM openjdk:11-jre
WORKDIR /opt/software/moany

# copy layered jars
COPY --from=build /workspace/dependencies/ ./
COPY --from=build /workspace/spring-boot-loader/ ./
COPY --from=build /workspace/snapshot-dependencies/ ./
COPY --from=build /workspace/application/ ./

RUN mkdir -p config
VOLUME /opt/software/moany/config
ENV SPRING_PROFILES_ACTIVE=datasource
ENV DB_URL=jdbc:h2:mem:moany
ENV DB_USER=sa
ENV DB_PASSWORD=password
ENV DB_PLATFORM=h2
EXPOSE 9080
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

