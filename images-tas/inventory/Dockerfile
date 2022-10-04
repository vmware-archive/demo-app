FROM golang:1.17 AS build
WORKDIR /src
ADD inventory /src

RUN make 

FROM alpine:3.6 as alpine
RUN apk add -U --no-cache ca-certificates

FROM scratch

COPY --from=alpine /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/
COPY --from=build /src/target/amd64/inventory /
COPY inventory.conf /conf/
EXPOSE 60001
CMD /inventory /conf/inventory.conf
