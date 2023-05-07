FROM alpine:3.17.3

RUN apk update \
    && apk upgrade \
    && apk add --update openjdk17-jre-headless

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
COPY './build/libs/mediator.jar' 'mediator.jar'
ENTRYPOINT ["java","-jar","mediator.jar"]