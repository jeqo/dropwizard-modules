package io.github.jeqo.dropwizard.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import io.opentracing.util.GlobalTracer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Dropwizard Factory to create managed Kafka clients: Producers, Consumers and AdminClient.
 */
public class KafkaClientFactory<K, V> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaClientFactory.class);

  @Valid
  @NotNull
  private String bootstrapServers;

  @Valid
  @JsonProperty
  private List<KafkaTopic> topics = new ArrayList<>();

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public void setBootstrapServers(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  public List<KafkaTopic> getTopics() {
    return topics;
  }

  public void setTopics(List<KafkaTopic> topics) {
    this.topics = topics;
  }

  /**
   * Creates a {@link KafkaConsumer} using configuration properties.
   *
   * @param environment       Dropwizard environment
   * @param keyDeserializer   Kafka Key Deserializer
   * @param valueDeserializer Kafka Value Deserializer
   * @param properties        Configuration properties
   * @return Dropwizard managed KafkaConsumer instance
   */
  public KafkaConsumer<K, V> buildConsumer(Environment environment,
                                           Deserializer<K> keyDeserializer,
                                           Deserializer<V> valueDeserializer,
                                           Properties properties) {
    final Properties configs = new Properties();
    configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configs.putAll(properties);

    final KafkaConsumer<K, V> kafkaConsumer =
        new KafkaConsumer<>(configs, keyDeserializer, valueDeserializer);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        kafkaConsumer.close();
      }
    });

    return kafkaConsumer;
  }

  /**
   * Creates a {@link KafkaConsumer} using configuration properties.
   * You will need to have a {@link Tracer} instance registered on {@link GlobalTracer} helper.
   *
   * @param environment       Dropwizard environment
   * @param keyDeserializer   Kafka Key Deserializer
   * @param valueDeserializer Kafka Value Deserializer
   * @param properties        Configuration properties
   * @return Dropwizard managed KafkaConsumer instance
   */
  public TracingKafkaConsumer<K, V> buildTracingConsumer(Environment environment,
                                                         Deserializer<K> keyDeserializer,
                                                         Deserializer<V> valueDeserializer,
                                                         Properties properties) {
    final Properties configs = new Properties();
    configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configs.putAll(properties);

    final KafkaConsumer<K, V> kafkaConsumer =
        new KafkaConsumer<>(configs, keyDeserializer, valueDeserializer);
    final Tracer tracer = GlobalTracer.get();
    final TracingKafkaConsumer<K, V> tracingKafkaConsumer =
        new TracingKafkaConsumer<>(kafkaConsumer, tracer);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        tracingKafkaConsumer.close();
      }
    });

    return tracingKafkaConsumer;
  }

  /**
   * Creates a {@link KafkaProducer} using configuration properties.
   *
   * @param environment     Dropwizard environment
   * @param keySerializer   Kafka Key Serializer
   * @param valueSerializer Kafka Value Serialized
   * @param properties      Configuration properties
   * @return Dropwizard managed Kafka Producer instance
   */
  public KafkaProducer<K, V> buildProducer(Environment environment,
                                           Serializer<K> keySerializer,
                                           Serializer<V> valueSerializer,
                                           Properties properties) {
    final Properties configs = new Properties();

    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configs.putAll(properties);

    KafkaProducer<K, V> producer =
        new KafkaProducer<>(configs, keySerializer, valueSerializer);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        producer.close();
      }
    });

    return producer;
  }

  /**
   * Creates a {@link TracingKafkaProducer} using configuration properties.
   * You will need to have a {@link Tracer} instance registered on {@link GlobalTracer} helper.
   *
   * @param environment     Dropwizard environment
   * @param keySerializer   Kafka Key Serializer
   * @param valueSerializer Kafka Value Serialized
   * @param properties      Properties
   * @return Dropwizard managed Kafka Producer instance
   */
  public TracingKafkaProducer<K, V> buildTracingProducer(Environment environment,
                                                         Serializer<K> keySerializer,
                                                         Serializer<V> valueSerializer,
                                                         Properties properties) {
    final Properties configs = new Properties();

    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configs.putAll(properties);

    KafkaProducer<K, V> producer =
        new KafkaProducer<>(configs, keySerializer, valueSerializer);
    final Tracer tracer = GlobalTracer.get();
    TracingKafkaProducer<K, V> tracingKafkaProducer = new TracingKafkaProducer<>(producer, tracer);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        tracingKafkaProducer.close();
      }
    });

    return tracingKafkaProducer;
  }

  /**
   * Creates a managed Kafka Admin Client.
   *
   * @param environment Dropwizard environment
   * @return Dropwizard managed Kafka Admin Client
   */
  public AdminClient buildAdminClient(Environment environment) {
    AdminClient adminClient = getAdminClient();

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() throws Exception {
      }

      @Override
      public void stop() throws Exception {
        adminClient.close();
      }
    });

    return adminClient;
  }

  private AdminClient getAdminClient() {
    final Properties configs = new Properties();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return KafkaAdminClient.create(configs);
  }

  /**
   * Creates or updated Kafka Topics.
   */
  public void prepareTopics() {
    AdminClient adminClient = getAdminClient();
    if (topics.isEmpty()) {
      LOGGER.info("Topic list is empty");
    }
    topics.forEach(topic -> topic.create(adminClient));
    adminClient.close();
  }
}
