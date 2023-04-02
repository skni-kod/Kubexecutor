FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && apt-get -y install python3

WORKDIR /app
COPY './build/libs/kubexecutor.jar' 'kubexecutor.jar'
ENTRYPOINT ["java","-jar","kubexecutor.jar"]