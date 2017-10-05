package io.github.jeqo.dropwizard.minio;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 *
 */
public class MinioClientFactory {
  @Valid
  @NotNull
  private String endpoint = "http://localhost:9000";
  @Valid
  @NotNull
  private String accessKey;
  @Valid
  @NotNull
  private String secretKey;

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public MinioClient build(){
    try {
      return new MinioClient(endpoint, accessKey, secretKey);
    } catch (InvalidEndpointException | InvalidPortException e) {
      //TODO fix
      e.printStackTrace();
      return null;
    }
  }
}
