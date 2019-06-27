FROM ubuntu:18.04

RUN apt update && apt install -y git openjdk-8-jdk-headless

ARG tarballName=megamek

RUN mkdir -p /work

COPY *.tar.gz /

RUN tar xvf *.tar.gz -C /work
RUN mv /work/$tarballName /megamek

RUN ls -l /
RUN ls -l /work
RUN ls -l /megamek

COPY clientsettings.xml /megamek/mmconf/
COPY gameoptions.xml /megamek/mmconf/

WORKDIR /megamek

CMD ["/megamek/startup", "-dedicated", "-port", "2346"]


