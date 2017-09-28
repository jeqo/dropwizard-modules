package io.github.jeqo.dropwizard.jaeger;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.opentracing.Tracer;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Dropwizard Factory for Jaeger Client
 */
public class JaegerClientFactory {

  @NotEmpty
  @JsonProperty
  private String host = "localhost";

  @Min(1)
  @Max(65535)
  @JsonProperty
  private int port = 6831;

  @Valid
  @JsonProperty
  private boolean logSpans = true;

  @Valid
  @JsonProperty
  private Integer flushIntervalMs = 1000;

  @Valid
  @JsonProperty
  private Integer maxQueueSize = 10000;

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

  public boolean isLogSpans() {
    return logSpans;
  }

  public void setLogSpans(boolean logSpans) {
    this.logSpans = logSpans;
  }

  public Integer getFlushIntervalMs() {
    return flushIntervalMs;
  }

  public void setFlushIntervalMs(Integer flushIntervalMs) {
    this.flushIntervalMs = flushIntervalMs;
  }

  public Integer getMaxQueueSize() {
    return maxQueueSize;
  }

  public void setMaxQueueSize(Integer maxQueueSize) {
    this.maxQueueSize = maxQueueSize;
  }

  /**
   * Creates a {@link Tracer} instance.
   *
   * @param componentName Component Name
   * @return Tracer instance
   */
  public Tracer build(String componentName) {
    return new com.uber.jaeger.Configuration(
        componentName,
        new com.uber.jaeger.Configuration.SamplerConfiguration("const", 1),
        new com.uber.jaeger.Configuration.ReporterConfiguration(
            logSpans,
            host,
            port,
            flushIntervalMs,   // flush interval in milliseconds
            maxQueueSize)  /*max buffered Spans*/)
        .getTracer();
  }
}
