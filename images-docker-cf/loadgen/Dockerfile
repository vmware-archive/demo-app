FROM openjdk:8-jre
ARG service_name
ENV service_name=$service_name
RUN echo "image name $service_name"
COPY ${service_name}.jar /
CMD java -jar /loadgen.jar shopping-service.apps.internal 50050 50150 2 15 0
#CMD java -jar /loadgen.jar shopping-service.cfapps.haas-463.pez.vmware.com 80 50150 2 15 0
