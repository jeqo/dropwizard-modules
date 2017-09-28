# Dropwizard Module: Apache Kafka

## Factories

### KafkaClientFactory

This factory instantiate Kafka Clients: Producer, Consumer, AdminClient.

#### Properties

| Property         | Type             | Default value  |
| ---------------- |:----------------:| --------------:|
| bootstrapServers | String           | localhost:9092 |
| topics           | List<KafkaTopic> |                |

##### KafkaTopics

| Property          | Type       | Default value  |
| ----------------- |:----------:| --------------:|
| name              | String     |                |
| partitions        | Integer    | 1              |
| replicationFactor | Short      | 1              |
| cleanupPolicy     | String     | delete         |
| retentionMs       | Long       | 604800000L     |
| retentionBytes    | Long       | -1L            |


#### Builders

* `#buildProducer(Environment environment,
                  Serializer<K> keySerializer,
                  Serializer<V> valueSerializer,
                  Properties properties)`

Creates a [KafkaProducer<K,V>](http://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html)
using configuration properties.

* `#buildTracingProducer(Environment environment,
                         Serializer<K> keySerializer,
                         Serializer<V> valueSerializer,
                         Properties properties)`

Creates a [TracingKafkaProducer<K,V>](https://github.com/opentracing-contrib/java-kafka-client/blob/master/src/main/java/io/opentracing/contrib/kafka/TracingKafkaProducer.java)
using configuration properties. You need a Tracer registered on GlobalTracer helper.

* `#buildConsumer(Environment environment,
                  Serializer<K> keySerializer,
                  Serializer<V> valueSerializer,
                  Properties properties)`

Creates a [KafkaConsumer<K,V>](http://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/producer/KafkaConsumer.html)
using configuration properties.

* `#buildTracingConsumer(Environment environment,
                         Deserializer<K> keyDeserializer,
                         Deserializer<V> valueDeserializer,
                         Properties properties)`

Creates a [TracingKafkaConsumer<K,V>](https://github.com/opentracing-contrib/java-kafka-client/blob/master/src/main/java/io/opentracing/contrib/kafka/TracingKafkaConsumer.java)
using configuration properties. You need a Tracer registered on GlobalTracer helper.


### KafkaStreamsFactory

