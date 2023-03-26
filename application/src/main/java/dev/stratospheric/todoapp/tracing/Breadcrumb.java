package dev.stratospheric.todoapp.tracing;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Breadcrumb {

  private String id;
  private String uri;
  private String username;
  private String timestamp;

  @DynamoDbPartitionKey
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @DynamoDbAttribute(value = "uri")
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  @DynamoDbAttribute(value = "username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @DynamoDbAttribute(value = "timestamp")
  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
