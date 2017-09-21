package io.github.jeqo.dropwizard.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class KafkaTopicFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTopicFactory.class);

  @Valid
  @NotNull
  private String name;
  @Valid
  @NotNull
  private Integer partitions;
  @Valid
  @NotNull
  private Short replicationFactor;
  private String cleanupPolicy = "delete";
  private Long retentionMs = 604800000L;
  private Long retentionBytes = -1L;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPartitions() {
    return partitions;
  }

  public void setPartitions(Integer partitions) {
    this.partitions = partitions;
  }

  public Short getReplicationFactor() {
    return replicationFactor;
  }

  public void setReplicationFactor(Short replicationFactor) {
    this.replicationFactor = replicationFactor;
  }

  public String getCleanupPolicy() {
    return cleanupPolicy;
  }

  public void setCleanupPolicy(String cleanupPolicy) {
    this.cleanupPolicy = cleanupPolicy;
  }

  public Long getRetentionMs() {
    return retentionMs;
  }

  public void setRetentionMs(Long retentionMs) {
    this.retentionMs = retentionMs;
  }

  public Long getRetentionBytes() {
    return retentionBytes;
  }

  public void setRetentionBytes(Long retentionBytes) {
    this.retentionBytes = retentionBytes;
  }

  void create(AdminClient adminClient) {
    try {
      final Collection<String> topics = adminClient.listTopics().names().get();
      boolean alreadyExist = topics.contains(name);
      if (alreadyExist) {
        LOGGER.warn("Topic {} already exists", name);
        Map<ConfigResource, Config> configs = new HashMap<>();
        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, name);
        Collection<ConfigEntry> entries = new ArrayList<>();
        //entries.add(new ConfigEntry("partitions", partitions.toString()));
        //entries.add(new ConfigEntry("replication.factor", replicationFactor.toString()));
        entries.add(new ConfigEntry("cleanup.policy", cleanupPolicy));
        entries.add(new ConfigEntry("retention.ms", retentionMs.toString()));
        entries.add(new ConfigEntry("retention.bytes", retentionBytes.toString()));
        Config config = new Config(entries);
        configs.put(configResource, config);
        adminClient.alterConfigs(configs);
      } else {
        createTopic(adminClient);
      }
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      LOGGER.error("Error creating topic {}", name, e);
    }
  }

  private void createTopic(AdminClient adminClient) throws ExecutionException, InterruptedException, TimeoutException {
    final NewTopic topic = new NewTopic(name, partitions, replicationFactor);
    final Map<String, String> topicConfigs = new HashMap<>();
    topicConfigs.put("cleanup.policy", cleanupPolicy);
    topicConfigs.put("retention.ms", retentionMs.toString());
    topicConfigs.put("retention.bytes", retentionBytes.toString());
    topic.configs(topicConfigs);
    adminClient.createTopics(Collections.singletonList(topic));
    LOGGER.info("Topic {} created", name);

  }
}
