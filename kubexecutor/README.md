To build:
```shell
./gradlew buildFatJar
```

To build container image:
```shell
VERSION=0.1
docker build -t kubexecutor:$VERSION -f Dockerfile .
```