FROM alpine:3.17.3

RUN apk update \
    && apk upgrade \
    && apk add --update openjdk17-jdk python3 nodejs

RUN addgroup \
    --gid 1001 \
    --system \
    executor \
    && adduser \
    --disabled-password \
    --home /app \
    --ingroup executor \
    --system \
    --uid 1001 \
    executor
USER executor

WORKDIR /app
COPY './build/libs/runner.jar' 'runner.jar'
ENTRYPOINT ["java","-jar","runner.jar"]