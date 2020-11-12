FROM java:8
EXPOSE 8080

VOLUME /tmp
ADD renren-fast.jar  /renren-fast.jar
RUN bash -c 'touch /renren-fast.jar'
ENTRYPOINT ["java","-jar","/renren-fast.jar"]
