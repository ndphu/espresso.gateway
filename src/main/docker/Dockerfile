FROM frolvlad/alpine-oraclejdk8:slim

EXPOSE 80

VOLUME /tmp

ADD espresso-gateway.jar app.jar

RUN sh -c 'touch /app.jar'


ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
