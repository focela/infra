## AdoptOpenJDK stopped publishing OpenJDK binaries; Eclipse Temurin is its successor and provides better stability.
FROM eclipse-temurin:21-jre

## Create the working directory and use it as the WORKDIR.
RUN mkdir -p /focela-server
WORKDIR /focela-server
## Copy the backend jar into the image.
COPY ./target/focela-server.jar app.jar

## Set the TZ timezone.
ENV TZ=Asia/Shanghai
## Set JAVA_OPTS; can be overridden via `docker run -e "JAVA_OPTS=..."`.
ENV JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

## Application arguments.
ENV ARGS=""

## Expose the backend port 48080.
EXPOSE 48080

## Start the backend.
CMD java ${JAVA_OPTS} -jar app.jar $ARGS
