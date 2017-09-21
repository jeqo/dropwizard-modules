package io.github.jeqo.dropwizard.jaeger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.opentracing.Tracer;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 *
 */
public class JaegerClientFactory {

  @NotEmpty
  private String host;

  @Min(1)
  @Max(65535)
  private int port = 9200;

  @JsonProperty
  public String getHost() {
    return host;
  }

  @JsonProperty
  public void setHost(String host) {
    this.host = host;
  }

  @JsonProperty
  public int getPort() {
    return port;
  }

  @JsonProperty
  public void setPort(int port) {
    this.port = port;
  }

  public Tracer build(String componentName) {
    return new com.uber.jaeger.Configuration(
        componentName,
        new com.uber.jaeger.Configuration.SamplerConfiguration("const", 1),
        new com.uber.jaeger.Configuration.ReporterConfiguration(
            true,  // logSpans
            host,
            port,
            1000,   // flush interval in milliseconds
            10000)  /*max buffered Spans*/)
        .getTracer();
  }
}
