FROM openjdk:8-jre
ARG SERVICE_NAME

RUN echo "image name $SERVICE_NAME"
EXPOSE  50051
COPY ${SERVICE_NAME}.jar /
COPY *.yaml /conf/
CMD java -jar /styling.jar server /conf/styling-app.yaml
