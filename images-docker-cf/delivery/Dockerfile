FROM openjdk:8-jre
ARG service_name
ENV service_name=$service_name
COPY *.yaml /conf/
RUN echo "image name $service_name"
COPY ${service_name}.jar /
