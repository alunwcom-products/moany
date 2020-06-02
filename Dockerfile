#
# build image
#

FROM openjdk:8-jdk-alpine as build

WORKDIR /workspace
COPY . /workspace
RUN sh gradlew build

#
# deployment image
#

FROM openjdk:8-jre-alpine

RUN mkdir -p /opt/software/
COPY --from=build /workspace/build/libs/moany-SNAPSHOT.war /opt/software/moany.war
ENTRYPOINT ["java","-jar","/opt/software/moany.war"]
