# azure-eventhub-demo

This is a working spring boot application which sends and receives messages to and from Azure Event Hub using 
spring-cloud-azure-eventhubs-stream-binder.

There is a spring boot test which accesses an event hub inside the azure cloud.
This is, because there is no local replacement like testcontainers or a docker image for Event Hubs.

## Conflicting dependency versions from azure sdk for java
This project also demonstrates, that the following azure dependency versions are incompatible:
- com.microsoft.azure:spring-cloud-azure-appconfiguration-config-web:1.2.7
- com.microsoft.azure:spring-cloud-azure-feature-management-web:1.2.7
- com.microsoft.azure:spring-cloud-azure-eventhubs-stream-binder:1.2.8

If all three depencencies are used with the same version 1.2.7, all works fine.
If the eventhub dependency is upgraded to 1.2.8, this leads to the following exception during exception:

```
Caused by: java.lang.NoSuchMethodError: 'void com.azure.core.util.logging.ClientLogger.warning(java.lang.String)'
   at com.azure.core.amqp.implementation.AmqpChannelProcessor.onSubscribe(AmqpChannelProcessor.java:69) ~[azure-core-amqp-1.3.0.jar:na]
   at reactor.core.publisher.Operators.reportThrowInSubscribe(Operators.java:224) ~[reactor-core-3.3.12.RELEASE.jar:3.3.12.RELEASE]
   at reactor.core.publisher.Flux.subscribe(Flux.java:8360) ~[reactor-core-3.3.12.RELEASE.jar:3.3.12.RELEASE]
```

 The updated eventhub library uses the `ClientLogger.warning(String)` method without varargs introduced in `com.azure:azure-core:1.4.0` with this commit:
 https://github.com/Azure/azure-sdk-for-java/commit/8e59d0aada39a3e6d166f367ca53e0de03ef91d4
 
 The question is: how should dependencies be declared to get consistent versions.
 Just using the same release for all dependencies does not work, if in this case the `spring-cloud-azure-eventhubs-stream-binder:1.2.8` is 
 needed, because there is no `1.2.8` release e.g. for `com.microsoft.azure:spring-cloud-azure-feature-management-web`.
 
 
 ## Needed cloud resources to get a working setup.

The test class `EventHubTest` needs the following Azure cloud resources, which are available within a free subscription:
 - Event Hub Namespace
 - Event Hub Instance
 - Storage Account
 - empty App Configuration

The setup process is described in this article:
https://docs.microsoft.com/de-de/azure/developer/java/spring-framework/configure-spring-cloud-stream-binder-java-app-azure-event-hub

Depending on the properties of the cloud resources, the entries in application.properties and bootstrap.properties need
to be updated. The files commited here do NOT contain the original connection strings.

## Analyze dependency changes

The complete list of all azure dependencies which get into the service can be displayed using the following command:

```
mvn dependency:list | grep " -- " | cut -c 11- | sort -u | grep azure
```

Changing the dependency version of `spring-cloud-azure-eventhubs-stream-binder` from 1.2.7 to 1.2.8 results in several
changed transitive dependencies as seen in the files [dependencies.ok](dependencies.ok)
and [dependencies.conflict](dependencies.conflict) 