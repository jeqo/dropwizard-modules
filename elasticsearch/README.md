# Dropwizard Module: Elasticsearch

## Factories

### ElasticsearchClientFactory

#### Properties

| Property         | Type                 | Default value  |
| ---------------- |:--------------------:| --------------:|
| host             | String               | localhost      |
| port             | Integer              | 9200           |
| indices          | ElasticsearchIndex[] |                |

#### ElasticsearchIndex

| Property         | Type                   | Default value  |
| ---------------- |:----------------------:| --------------:|
| name             | String                 | localhost      |
| settings         | Settings               | 9200           |
| mappings         | ElasticsearchMapping[] |                |

#### Settings

| Property         | Type               | Default value  |
| ---------------- |:------------------:| --------------:|
| numberOfShards   | Integer            | 1              |
| numberOfReplicas | Integer            | 1              |


#### ElasticsearchMapping

| Property   | Type                 | Default value  |
| ---------- |:--------------------:| --------------:|
| name       | String               |                |
| properties | Map<String, String>  | 1              |


### Builders

//TODO