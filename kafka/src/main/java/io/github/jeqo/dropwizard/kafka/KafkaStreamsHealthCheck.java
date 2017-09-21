package io.github.jeqo.dropwizard.kafka;

import com.codahale.metrics.health.HealthCheck;
import org.apache.kafka.streams.KafkaStreams;

/**
 *
 */
public class KafkaStreamsHealthCheck extends HealthCheck {
  private final KafkaStreams kafkaStreams;

  public KafkaStreamsHealthCheck(KafkaStreams kafkaStreams) {
    this.kafkaStreams = kafkaStreams;
  }


  @Override
  protected Result check() throws Exception {
    if (!kafkaStreams.state().isRunning()) {
      return Result.unhealthy("KafkaStreams is not running");
    } else {
      return Result.healthy();
    }
  }
}
