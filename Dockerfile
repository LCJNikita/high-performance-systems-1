FROM openjdk:21-buster
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080

#FROM openjdk:21-buster AS builder
#
#RUN apt-get update -y
#RUN apt-get install -y binutils
#
#WORKDIR /app
#
#COPY . .
#
#RUN mvn install
#
#ENTRYPOINT ["java", "-jar", "/app/build/libs/HPS-project-0.0.1-SNAPSHOT.jar"]