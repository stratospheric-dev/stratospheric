package dev.stratospheric.todoapp;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@SpringBootApplication(exclude = ContextInstanceDataAutoConfiguration.class)
public class TodoApplication {

  private static final Logger LOG = LoggerFactory.getLogger(TodoApplication.class.getName());

  private final AmazonDynamoDB amazonDynamoDB;

  private final String breadcrumbTableName;

  public TodoApplication(
    AmazonDynamoDB amazonDynamoDB,
    @Value("${custom.breadcrumb-table-name}") String breadcrumbTableName
  ) {
    this.amazonDynamoDB = amazonDynamoDB;
    this.breadcrumbTableName = breadcrumbTableName;
  }

  public static void main(String[] args) {
    SpringApplication.run(TodoApplication.class, args);
  }

  @PostConstruct
  public void initializeDynamoDBTables() {
    DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    try {
      List<KeySchemaElement> keySchemaElementList = Collections.singletonList(
        new KeySchemaElement("id", KeyType.HASH)
      );
      List<AttributeDefinition> attributeDefinitionList = Collections.singletonList(
        new AttributeDefinition("id", ScalarAttributeType.S)
      );
      Table table = dynamoDB.createTable(
        breadcrumbTableName,
        keySchemaElementList,
        attributeDefinitionList,
        new ProvisionedThroughput(10L, 10L)
      );
      table.waitForActive();
    } catch (Exception e) {
      LOG.error("Unable to create DynamoDB table: {}", e.getMessage());
    }
  }
}
