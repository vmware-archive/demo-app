FROM openjdk:8-jre
ARG SERVICE_NAME

RUN echo "image name $SERVICE_NAME"
COPY ${SERVICE_NAME}.jar /
COPY *.yaml /conf/
EXPOSE 50052
CMD java -jar /printing.jar /conf/printing-app.yaml
