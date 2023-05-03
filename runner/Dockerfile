FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && apt-get -y install python3 && apt-get -y install nodejs

RUN groupadd \
    --gid 1001 \
    --system \
    executor \
    && useradd \
    --create-home \
    --gid executor \
    --home /app \
    --no-log-init \
    --system \
    --uid 1001 \
    executor
USER executor

WORKDIR /app
COPY './build/libs/runner.jar' 'runner.jar'
ENTRYPOINT ["java","-jar","runner.jar"]