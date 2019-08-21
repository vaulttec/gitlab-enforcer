FROM openjdk:11-slim

EXPOSE 8080

ENV APP_HOME /app
ENV JAVA_OPTS=""

RUN adduser -S -u 1000 springboot && \
    mkdir -p $APP_HOME/logs && \
    chown -R springboot $APP_HOME
USER springboot

WORKDIR $APP_HOME

ARG JAR_FILE
COPY ${JAR_FILE} $APP_HOME/app.jar

VOLUME /tmp $APP_HOME/config $APP_HOME/logs

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dlogging.path=$APP_HOME/logs -jar app.jar" ]
