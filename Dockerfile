ARG JDK_VERSION=17
ARG VENN_PROJECT

FROM clojure:openjdk-${JDK_VERSION} AS build

WORKDIR /
COPY . .

RUN clojure -T:build uberjar :project $VENN_PROJECT :uber-file target/$VENN_PROJECT-standalone.jar

FROM azul/zulu-openjdk-alpine:${JDK_VERSION}

COPY --from=build target/${VENN_PROJECT}-standalone.jar /app/${VENN_PROJECT}-standalone.jar

EXPOSE 8080

ENTRYPOINT exec java -jar /app/${VENN_PROJECT}-standalone.jar
