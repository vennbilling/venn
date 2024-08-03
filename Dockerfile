# Build uberjar
ARG JDK_VERSION

FROM clojure:temurin-${JDK_VERSION}-tools-deps-noble AS build

ARG VENN_PROJECT

RUN mkdir build

WORKDIR /build

COPY . .

RUN clojure -T:build uberjar :project ${VENN_PROJECT} :uber-file target/${VENN_PROJECT}-standalone.jar

# Runner
ARG JDK_VERSION

FROM azul/zulu-openjdk-alpine:${JDK_VERSION}

ARG VENN_PROJECT

RUN mkdir app

WORKDIR /app

COPY --from=build /build/projects/${VENN_PROJECT}/target/${VENN_PROJECT}-standalone.jar .

EXPOSE 8080

ENTRYPOINT exec java -jar /app/${VENN_PROJECT}-standalone.jar
