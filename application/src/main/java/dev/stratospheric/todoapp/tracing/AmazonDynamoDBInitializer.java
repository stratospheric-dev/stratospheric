package dev.stratospheric.todoapp.tracing;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmazonDynamoDBInitializer {

  private static final Logger LOG = LoggerFactory.getLogger(AmazonDynamoDBInitializer.class.getName());

  private final AmazonDynamoDB amazonDynamoDB;
  private final String breadcrumbTableName;

  public AmazonDynamoDBInitializer(
    AmazonDynamoDB amazonDynamoDB,
    @Value("${custom.breadcrumb-table-name}") String breadcrumbTableName
  ) {
    this.amazonDynamoDB = amazonDynamoDB;
    this.breadcrumbTableName = breadcrumbTableName;
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
