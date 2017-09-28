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
import org.elasticsearch.client.RestHighLevelClient;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dropwizard Elasticsearch Client Factory
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

  /**
   * Creates a managed low level Elasticsearch REST client {@link RestClient}.
   *
   * @param environment Dropwizard Environment
   * @return Low Level Elasticsearch instance
   */
  public RestClient buildLowLevelClient(Environment environment) {
    final RestClient client = getRestClient();

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

  /**
   * Creates a managed high level Elasticsearch REST client {@link RestHighLevelClient}.
   *
   * @param environment Dropwizard Environment
   * @return High Level Elasticsearch instance
   */
  public RestHighLevelClient buildHighLevelClient(Environment environment) {
    final RestClient lowLevelRestClient = getRestClient();
    final RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        try {
          lowLevelRestClient.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    return client;
  }

  /**
   * Creates a managed low level Elasticsearch REST client {@link RestClient} instrumented with
   * OpenTracing.
   * You will need to have a {@link Tracer} instance registered on {@link GlobalTracer} helper.
   *
   * @param environment Dropwizard Environment
   * @return Low Level Elasticsearch instance
   */
  public RestClient buildLowLevelClientWithTracing(Environment environment) {
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

  /**
   * Creates a managed high level Elasticsearch REST client {@link RestHighLevelClient}instrumented with
   * OpenTracing.
   * You will need to have a {@link Tracer} instance registered on {@link GlobalTracer} helper..
   *
   * @param environment Dropwizard Environment
   * @return High Level Elasticsearch instance
   */
  public RestHighLevelClient buildHighLevelClientWithTracing(Environment environment) {
    final HttpHost httpHost = new HttpHost(host, port);
    final Tracer tracer = GlobalTracer.get();
    final RestClientBuilder restClientBuilder =
        RestClient.builder(httpHost)
            .setHttpClientConfigCallback(new TracingHttpClientConfigCallback(tracer));
    final RestClient lowLevelRestClient = restClientBuilder.build();
    final RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        try {
          lowLevelRestClient.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    return client;
  }

  /**
   * Creates Indexes and Mappings
   * @param environment Dropwizard Environment
   */
  public void prepareIndexes(Environment environment) {
    final RestClient restClient = getRestClient();
    final ObjectMapper objectMapper = environment.getObjectMapper();
    indices.forEach(index -> index.create(restClient, objectMapper));
  }

  private RestClient getRestClient() {
    final HttpHost httpHost = new HttpHost(host, port);
    final RestClientBuilder restClientBuilder = RestClient.builder(httpHost);
    return restClientBuilder.build();
  }
}
