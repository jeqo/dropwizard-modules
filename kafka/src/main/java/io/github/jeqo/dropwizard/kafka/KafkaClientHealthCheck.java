package io.github.jeqo.dropwizard.kafka;

import com.codahale.metrics.health.HealthCheck;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;

/**
 *
 */
public class KafkaClientHealthCheck extends HealthCheck {
  private final AdminClient adminClient;

  public KafkaClientHealthCheck(AdminClient adminClient) {
    this.adminClient = adminClient;
  }

  @Override
  protected Result check() throws Exception {
    try {
      DescribeClusterResult response = adminClient.describeCluster();
      final boolean nodesNotEmpty = !response.nodes().get().isEmpty();
      final boolean clusterIdAvailable = response.clusterId() != null;
      if (clusterIdAvailable && nodesNotEmpty) {
        return Result.healthy();
      } else {
        return Result.unhealthy("Error connecting to Kafka Cluster");
      }
    } catch (Exception e) {
      return Result.unhealthy("Error connecting to Kafka Cluster", e);
    }
  }
}
