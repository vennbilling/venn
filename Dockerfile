ARG JDK_VERSION

FROM clojure:openjdk-${JDK_VERSION} AS build

ARG JDK_VERSION
ARG VENN_PROJECT

WORKDIR /
COPY . .

RUN clojure -T:build uberjar :project "$VENN_PROJECT" :uber-file target/"$VENN_PROJECT"-standalone.jar

FROM azul/zulu-openjdk-alpine:${JDK_VERSION}

COPY --from=build target/${VENN_PROJECT}-standalone.jar /app/${VENN_PROJECT}-standalone.jar

EXPOSE 8080

ENTRYPOINT exec java -jar /app/${VENN_PROJECT}-standalone.jar
