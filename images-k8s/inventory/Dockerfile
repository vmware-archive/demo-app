FROM golang:1.17 AS build
WORKDIR /src
ADD inventory /src

RUN make 
# RUN ls target/amd64/inventory

FROM alpine:3.6 as alpine

RUN apk add -U --no-cache ca-certificates

FROM scratch
# FROM alpine:3.6

COPY --from=alpine /etc/ssl/certs/ca-certificates.crt /etc/ssl/certs/

COPY --from=build /src/target/amd64/inventory /
