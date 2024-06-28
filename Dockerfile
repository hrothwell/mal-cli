FROM alpine:3.17.7

RUN apk update && apk upgrade && apk add curl && apk add openjdk17

COPY ./build/libs/mal-cli-1.0-all.jar ./

# Port used for redirect during login flow
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./mal-cli-1.0-all.jar"]
