FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD protocol_supply-1.0.0-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
RUN echo "Asia/Shanghai" > /etc/timezone
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /hainan/protocol_supply/protocol_supply-1.0.0-SNAPSHOT.jar" ]