FROM openjdk:8-jre
ARG SERVICE_NAME
RUN echo "service name: $SERVICE_NAME"
COPY ${SERVICE_NAME}.jar /
ARG TAS_DOMAIN
ENV TAS_DOMAIN=$TAS_DOMAIN
RUN echo "tas domain: $TAS_DOMAIN"
#CMD java -jar /loadgen.jar shopping 50050 50150 2 15 0
CMD java -jar /loadgen.jar shopping${TAS_DOMAIN} 50050 50150 2 15 0