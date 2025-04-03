ARG _BRANCH=develop
FROM eu.gcr.io/example-cloud-infrastructure/neo-service-11:latest-$_BRANCH-UTC

COPY target/*.jar /opt/example/service.jar
EXPOSE 8010
COPY oauth.json /oauth.json
