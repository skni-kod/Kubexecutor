To build:
```shell
./gradlew buildFatJar
```

To build container image:
```shell
 docker build -t kubexecutor:$(git rev-parse --short HEAD) -t kubexecutor:latest .
```