package io.github.jeqo.dropwizard.kafka;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class KafkaStreamsStoreHelper {
  private static final Logger LOGGER = Logger.getLogger(KafkaStreamsStoreHelper.class.getName());

  public static <T> T waitUntilStoreIsQueryable(
      final String storeName,
      final QueryableStoreType<T> queryableStoreType,
      final KafkaStreams streams)
      throws InterruptedException {
    while (true) {
      try {
        return streams.store(storeName, queryableStoreType);
      } catch (InvalidStateStoreException ignored) {
        LOGGER.log(Level.WARNING, "error with state");
        // store not yet ready for querying
        Thread.sleep(100);
      }
    }
  }

}
