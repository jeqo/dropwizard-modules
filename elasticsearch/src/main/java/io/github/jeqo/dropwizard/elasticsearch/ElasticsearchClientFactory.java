package io.github.jeqo.dropwizard.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import io.opentracing.Tracer;
import io.opentracing.contrib.elasticsearch.TracingHttpClientConfigCallback;
import io.opentracing.util.GlobalTracer;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ElasticsearchClientFactory {
  @NotEmpty
  @JsonProperty
  private String host;

  @Min(1)
  @Max(65535)
  @JsonProperty
  private int port = 9200;

  @JsonProperty
  private List<ElasticsearchIndex> indices = new ArrayList<>();

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }


  public List<ElasticsearchIndex> getIndices() {
    return indices;
  }

  public void setIndices(List<ElasticsearchIndex> indices) {
    this.indices = indices;
  }

  public RestClient build(Environment environment) {
    final HttpHost httpHost = new HttpHost(host, port);
    final RestClientBuilder restClientBuilder = RestClient.builder(httpHost);
    final RestClient client = restClientBuilder.build();

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        try {
          client.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    return client;
  }

  public RestClient buildWithTracing(Environment environment) {
    final HttpHost httpHost = new HttpHost(host, port);
    final Tracer tracer = GlobalTracer.get();
    final RestClientBuilder restClientBuilder =
        RestClient.builder(httpHost)
            .setHttpClientConfigCallback(new TracingHttpClientConfigCallback(tracer));
    final RestClient client = restClientBuilder.build();

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        try {
          client.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    return client;
  }

  public void prepareIndexes(Environment environment) {
    final RestClient restClient = build(environment);
    final ObjectMapper objectMapper = environment.getObjectMapper();
    indices.forEach(index -> index.create(restClient, objectMapper));
  }
}
