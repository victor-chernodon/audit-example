# audit-example

## Audit Framework

Audit Framework provides a centralized solution for storing and retrieving of audit event log messages.

## Framework project structure
There following sub-projects are part of the framework:
1. Audit service - micro service designed to handle the storing and retrieval of audit messages. This is the core componenet of the framework. It is located under git@github.com:example/audit-api.git.
2. Audit common - common libraries meant to be shared between components of Audit Framework. It is located under git@github.com:example/audit-common.git.
3. Audit client - client implementation for different languages used at Auditexample (java, php). The client is responsible for sending audit log messages to audit service, and for calling the audit service for stored messages. It is located under git@github.com:example/audit-client.git.
