ARG JDK_VERSION=17

FROM clojure:openjdk-${JDK_VERSION} AS build

WORKDIR /
COPY . .

RUN clojure -T:build all

FROM azul/zulu-openjdk-alpine:${JDK_VERSION}

COPY --from=build /target/agent-standalone.jar /app/agent-standalone.jar

EXPOSE 8080

ENTRYPOINT exec java -jar /app/agent-standalone.jar
