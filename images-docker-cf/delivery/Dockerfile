FROM openjdk:8-jre
ARG service_name
ENV service_name=$service_name
COPY *.yaml /conf/
RUN echo "image name $service_name"
COPY ${service_name}.jar /
EXPOSE 50054
CMD java -jar /delivery.jar --spring.config.location=/conf/delivery-app.yaml
