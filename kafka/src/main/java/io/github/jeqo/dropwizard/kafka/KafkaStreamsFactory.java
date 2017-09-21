package io.github.jeqo.dropwizard.kafka;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Properties;

/**
 *
 */
public class KafkaStreamsFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaClientFactory.class);

  @Valid
  @NotNull
  private String bootstrapServers;

  @Valid
  private String stateDir;

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public void setBootstrapServers(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  public String getStateDir() {
    return stateDir;
  }

  public void setStateDir(String stateDir) {
    this.stateDir = stateDir;
  }

  public KafkaStreams buildStreams(Environment environment,
                                   KStreamBuilder builder,
                                   Properties properties) {
    final Properties configs = new Properties();
    configs.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configs.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
    configs.putAll(properties);

    final KafkaStreams kafkaStreams = new KafkaStreams(builder, configs);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() throws Exception {
        kafkaStreams.start();
        LOGGER.info("Kafka Streams State: {}", kafkaStreams.state());
      }

      @Override
      public void stop() throws Exception {
        kafkaStreams.close();
      }
    });

    return kafkaStreams;
  }
}
