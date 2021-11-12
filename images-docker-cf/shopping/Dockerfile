FROM openjdk:8-jre
ARG service_name
ENV service_name=$service_name
RUN echo "image name $service_name"
COPY ${service_name}.jar /
COPY *.yaml /conf/
EXPOSE 50050
EXPOSE 50150
CMD java -jar /shopping.jar server /conf/shopping-app.yaml
