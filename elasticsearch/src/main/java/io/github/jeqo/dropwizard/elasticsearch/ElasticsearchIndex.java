package io.github.jeqo.dropwizard.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class ElasticsearchIndex {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchIndex.class);

  @Valid
  @NotNull
  @JsonProperty
  private String name;
  @Valid
  @JsonProperty
  private Settings settings;

  @JsonProperty
  private List<ElasticsearchMapping> mappings = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
    this.settings = settings;
  }

  public List<ElasticsearchMapping> getMappings() {
    return mappings;
  }

  public void setMappings(List<ElasticsearchMapping> mappings) {
    this.mappings = mappings;
  }

  void create(RestClient restClient, ObjectMapper objectMapper) {
    try {
      Response getIndexResponse = restClient.performRequest("GET", "/" + name);
      if (getIndexResponse.getStatusLine().getStatusCode() == 404) {
        LOGGER.warn("Elasticsearch Index not found");
        LOGGER.warn("Creating Elasticsearch Index {}", name);
        final JsonNode jsonNode = getJson(objectMapper);
        final String json = objectMapper.writeValueAsString(jsonNode);
        LOGGER.info("Index {}: {}", name, json);
        final HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
        final Response putIndexResponse =
            restClient.performRequest("PUT", "/" + name, Collections.emptyMap(), entity);
        if (putIndexResponse.getStatusLine().getStatusCode() == 201) {
          LOGGER.warn("Elasticsearch Index {} created", name);
          mappings.forEach(mapping -> mapping.create(name, restClient, objectMapper));
        }
      } else {
        mappings.forEach(mapping -> mapping.create(name, restClient, objectMapper));
      }
    } catch (IOException e) {
      LOGGER.warn("Error creating Index {}", name, e);
    }
  }

  private JsonNode getJson(ObjectMapper objectMapper) {
    return
        objectMapper.createObjectNode()
            .set("settings", settings.getSettingsNode(objectMapper));
  }

  private class Settings {
    private Integer numberOfShards = 1;
    private Integer numberOfReplicas = 1;

    ObjectNode getSettingsNode(ObjectMapper objectMapper) {
      return
          objectMapper.createObjectNode()
              .put("number_of_shards", numberOfShards)
              .put("number_of_replicas", numberOfReplicas);
    }
  }
}
