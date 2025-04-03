## Audit Framework

Audit Framework provides a centralized solution for storing and retrieving of audit event log messages.

The complete details of the centralized audit solution are here - https://example.atlassian.net/wiki/spaces/RD/pages/1427243009/Centralized+Audit+-+Solution

## Framework project structure
There following sub-projects are part of the framework:
1. Audit service - micro service designed to handle the storing and retrieval of audit messages. This is the core componenet of the framework. It is located under git@github.com:example/audit-api.git. 
2. Audit common - common libraries meant to be shared between components of Audit Framework. It is located under git@github.com:example/audit-common.git. 
3. Audit client - client implementation for different languages used at Auditexample (java, php). The client is responsible for sending audit log messages to audit service, and for calling the audit service for stored messages. It is located under git@github.com:example/audit-client.git.

## Audit service

Audit service provides a centralized solution for storing and retrieving of audit event log messages.

## Build the Project
```
mvn clean install 
```
## Running locally

In order to be able to run the service locally, the local DynamoDB and Kafka instances are required. 
1. Dynamo DB - It could be achieved either by installing it (https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html)
, or running it as a docker container. (https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.Docker.html)
2. Kafka can be either be installed locally (https://kafka.apache.org/), or many kafka docker images are available as well (for example, https://github.com/wurstmeister/kafka-docker)