FROM openjdk:8-jre
ARG SERVICE_NAME

RUN echo "image name $SERVICE_NAME"
COPY ${SERVICE_NAME}.jar /
COPY *.yaml /conf/
EXPOSE 50054
CMD  java -jar /delivery.jar  --spring.config.location=/conf/delivery-app.yaml
