FROM java:8-jdk-alpine

COPY ./target/Backend-Sambatech-Encoding-1.0.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch Backend-Sambatech-Encoding-1.0.jar'

ENTRYPOINT ["java","-jar","Backend-Sambatech-Encoding-1.0.jar"]