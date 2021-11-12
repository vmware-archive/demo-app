FROM openjdk:8-jre
ARG service_name
ENV service_name=$service_name
RUN echo "image name $service_name"
COPY ${service_name}.jar /
COPY *.yaml /conf/
CMD java -jar /printing.jar /conf/printing-app.yaml
