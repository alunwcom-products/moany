#
# build image
#

FROM openjdk:11-jdk as build
WORKDIR /workspace
COPY . /workspace
RUN sh gradlew build

#
# deployment image
#

FROM openjdk:11-jre
RUN mkdir -p /opt/software/moany/config
VOLUME /opt/software/moany/config
COPY --from=build /workspace/build/libs/moany-SNAPSHOT.war /opt/software/moany/moany.war
ENV SPRING_PROFILES_ACTIVE="default" \
    DB_URL="jdbc:h2:mem:moany" \
    DB_USER="sa" \
    DB_PASSWORD="password" \
    DB_PLATFORM="h2"
EXPOSE 9080
CMD ["java","-jar","/opt/software/moany/moany.war"]
