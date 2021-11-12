FROM openjdk:8-jre
ARG service_name
ENV service_name=$service_name
RUN echo "image name $service_name"
EXPOSE  50051
COPY ${service_name}.jar /
COPY *.yaml /conf/
CMD java -jar /styling.jar server /conf/styling-app.yaml
