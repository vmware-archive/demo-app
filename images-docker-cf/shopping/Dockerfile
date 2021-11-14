FROM openjdk:8-jre
ARG SERVICE_NAME

RUN echo "image name $SERVICE_NAME"
COPY ${SERVICE_NAME}.jar /
COPY *.yaml /conf/
EXPOSE 50050
EXPOSE 50150
CMD java -jar /shopping.jar server /conf/shopping-app.yaml
