package io.github.jeqo.dropwizard.opentracing;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.jeqo.dropwizard.jaeger.JaegerClientFactory;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;

/**
 *
 */
public class OpenTracingFactory {

  @JsonProperty("provider")
  private String provider = TracingProvider.MOCK.name();

  @JsonProperty("jaeger")
  private JaegerClientFactory jaegerClientFactory;

  public Tracer build(String moduleName) {
    switch (TracingProvider.valueOf(provider)) {
      case JAEGER:
        return jaegerClientFactory.build(moduleName);
      default:
        return new MockTracer();
    }
  }

  enum TracingProvider {
    JAEGER, MOCK
  }
}
