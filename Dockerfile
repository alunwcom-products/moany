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
COPY --from=build /workspace/build/libs/moany-SNAPSHOT.war /opt/software/moany/moany.war
ENV spring_profiles_active=mysql
EXPOSE 9080
VOLUME /opt/software/moany/config
CMD ["java","-jar","/opt/software/moany/moany.war"]
