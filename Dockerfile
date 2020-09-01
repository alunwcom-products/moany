#
# build image
#

FROM openjdk:11-jdk as build
WORKDIR /workspace
COPY . /workspace
RUN sh gradlew build
RUN java -Djarmode=layertools -jar /workspace/build/libs/moany-SNAPSHOT.jar

#
# deployment image
#

FROM openjdk:11-jre
RUN mkdir -p /opt/software/moany/config
VOLUME /opt/software/moany/config
COPY --from=build /workspace/build/libs/moany-SNAPSHOT.jar /opt/software/moany/moany.jar
ENV SPRING_PROFILES_ACTIVE="datasource" \
    DB_URL="jdbc:h2:mem:moany" \
    DB_USER="sa" \
    DB_PASSWORD="password" \
    DB_PLATFORM="h2"
EXPOSE 9080
CMD ["java","-jar","/opt/software/moany/moany.jar"]
