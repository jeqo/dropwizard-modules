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
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ElasticsearchMapping {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchMapping.class);
  @Valid
  @JsonProperty
  private String name;
  @Valid
  @JsonProperty
  private Map<String, Property> properties = new HashMap<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Property> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Property> properties) {
    this.properties = properties;
  }

  private JsonNode getJson(ObjectMapper objectMapper) {
    ObjectNode propertiesNode = objectMapper.createObjectNode();

    properties.forEach((key, value) -> propertiesNode.set(key, value.getJsonObject(objectMapper)));

    return objectMapper.createObjectNode().set("properties", propertiesNode);
  }

  void create(String index, RestClient restClient, ObjectMapper objectMapper) {
    try {
      final JsonNode jsonNode = getJson(objectMapper);
      final String json = objectMapper.writeValueAsString(jsonNode);
      LOGGER.info("Mapping {}: {}", name, json);
      final HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
      final Response response =
          restClient.performRequest(
              "PUT",
              "/" + index + "/_mapping/" + name,
              Collections.emptyMap(),
              entity);

      if (response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 200) {
        LOGGER.info("Elasticsearch Mapping {} on Index {} created", name, index);
      } else {
        LOGGER.error("Elasticsearch Mapping {} on Index {} not created: " + response.getStatusLine().getReasonPhrase());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static class Property {
    private String type = "text";
    private String index = "analyzed";
    private boolean fielddata = false;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getIndex() {
      return index;
    }

    public void setIndex(String index) {
      this.index = index;
    }

    public boolean isFielddata() {
      return fielddata;
    }

    public void setFielddata(boolean fielddata) {
      this.fielddata = fielddata;
    }

    JsonNode getJsonObject(ObjectMapper objectMapper) {
      final ObjectNode jsonNode = objectMapper.createObjectNode()
          .put("type", type)
          .put("index", index);
      if (fielddata) {
        jsonNode.put("fielddata", true);
      }
      return jsonNode;
    }
  }
}
