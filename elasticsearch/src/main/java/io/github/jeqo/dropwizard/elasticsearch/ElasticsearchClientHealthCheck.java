package io.github.jeqo.dropwizard.elasticsearch;

import com.codahale.metrics.health.HealthCheck;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

/**
 *
 */
public class ElasticsearchClientHealthCheck extends HealthCheck {

  private final RestClient restClient;

  public ElasticsearchClientHealthCheck(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  protected Result check() throws Exception {
    Response response = restClient.performRequest("GET", "_nodes");
    if (response.getStatusLine().getStatusCode() == 200) {
      return Result.healthy();
    } else {
      return Result.unhealthy("Elasticsearch is not available");
    }
  }
}
