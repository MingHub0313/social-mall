FROM java:latest
VOLUME /tmp
ADD *.jar app.jar
EXPOSE 88
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]
