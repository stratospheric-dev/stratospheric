package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableEncryption;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.constructs.Construct;

public class BreadcrumbsDynamoTable extends Construct {

  public BreadcrumbsDynamoTable(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final BreadcrumbsDynamoTableInputParameters inputParameters
  ) {

    super(scope, id);

    new Table(
      this,
      "BreadcrumbsDynamoDbTable",
      TableProps.builder()
        .partitionKey(
          Attribute.builder().type(AttributeType.STRING).name("id").build())
        .tableName(applicationEnvironment.prefix(inputParameters.tableName))
        .encryption(TableEncryption.AWS_MANAGED)
        .billingMode(BillingMode.PROVISIONED)
        .readCapacity(10)
        .writeCapacity(10)
        .build());
  }


  record BreadcrumbsDynamoTableInputParameters(String tableName) {
  }
}
