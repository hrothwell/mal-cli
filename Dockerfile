FROM alpine:3.17.7

RUN apk update && apk upgrade && apk add curl && apk add openjdk17

COPY ./build/libs/mal-cli-1.0-all.jar ./

ENTRYPOINT ["java", "-jar", "./mal-cli-1.0-all.jar"]

# TODO This is not fully viable yet, there would need to be a process to open URLs in the host machine from the docker image. 