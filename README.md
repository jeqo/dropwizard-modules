# DropWizard Modules

Dropwizard wizard modules to reuse common factories and health checks
to different back-ends.

Functionality is mean to be simple:

Factories just expose enough configuration to create clients and
add client instances as Managed objects.

Health-checks use client instances to validate that back-ends are
up and running.

## Modules supported

* Camel
* Elasticsearch
* [Kafka](kafka/README.md)
* Jaeger
* OpenTracing