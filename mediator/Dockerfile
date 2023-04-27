FROM eclipse-temurin:17-jdk-jammy

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
COPY './build/libs/mediator.jar' 'mediator.jar'
ENTRYPOINT ["java","-jar","mediator.jar"]