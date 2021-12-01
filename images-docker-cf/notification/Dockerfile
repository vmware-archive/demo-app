FROM openjdk:8-jre
ARG SERVICE_NAME

RUN echo "image name $SERVICE_NAME"
COPY ${SERVICE_NAME}.jar /
COPY *.yaml /conf/
EXPOSE 50056
CMD java -jar /notification.jar --spring.config.location=/conf/notification-app.yaml
