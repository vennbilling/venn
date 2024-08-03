# Build uberjar
ARG JDK_VERSION

FROM clojure:temurin-${JDK_VERSION}-tools-deps-noble AS build

ARG VENN_PROJECT

WORKDIR /
COPY . .

RUN clojure -T:build uberjar :project ${VENN_PROJECT} :uber-file target/${VENN_PROJECT}-standalone.jar

# Runner
ARG JDK_VERSION

FROM azul/zulu-openjdk-alpine:${JDK_VERSION}

ARG VENN_PROJECT

COPY --from=build projects/${VENN_PROJECT}/target/${VENN_PROJECT}-standalone.jar /app/${VENN_PROJECT}-standalone.jar

EXPOSE 8080

ENTRYPOINT exec java -jar /app/${VENN_PROJECT}-standalone.jar
