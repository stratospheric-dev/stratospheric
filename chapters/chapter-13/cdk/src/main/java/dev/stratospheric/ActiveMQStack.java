package dev.stratospheric;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.amazonmq.CfnBroker;
import software.amazon.awscdk.services.amazonmq.CfnBrokerProps;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretStringGenerator;

import java.util.ArrayList;
import java.util.List;

public class ActiveMQStack extends Stack {

  public ActiveMQStack(
    final Construct scope,
    final String id,
    final Environment environment,
    final String username
  ) {
    super(
      scope,
      id,
      StackProps.builder()
        .stackName("ActiveMQCdkStack")
        .env(environment)
        .build()
    );

    ISecret secret = Secret.Builder.create(this, "activeMQSecret")
      .secretName("ActiveMQSecret")
      .description("Credentials for the ActiveMQ instance")
      .generateSecretString(
        SecretStringGenerator
          .builder()
          .secretStringTemplate(String.format("{\"username\": \"%s\"}", username))
          .generateStringKey("password")
          .passwordLength(32)
          .build()
      )
      .build();
    String password = secret.secretValueFromJson("password").toString();

    List<User> userList = new ArrayList<>();
    userList.add(new User(
      username,
      password
    ));

    String name = "stratospheric-message-broker";
    CfnBroker cfnBroker = new CfnBroker(
      this,
      name,
      CfnBrokerProps
        .builder()
        .brokerName(name)
        .hostInstanceType("mq.t2.micro")
        .engineType("ACTIVEMQ")
        .engineVersion("5.15.14")
        .authenticationStrategy("SIMPLE")
        .users(userList)
        .publiclyAccessible(true)
        .autoMinorVersionUpgrade(true)
        .deploymentMode("ACTIVE_STANDBY_MULTI_AZ")
        .build()
    );

    new CfnOutput(this,
      "ActiveMQStompEndpoint",
      CfnOutputProps
        .builder()
        .value(cfnBroker.getAttrStompEndpoints().get(0))
        .build()
    );

    new CfnOutput(this,
      "ActiveMQUsername",
      CfnOutputProps
        .builder()
        .value(username)
        .build()
    );

    new CfnOutput(this,
      "ActiveMQPassword",
      CfnOutputProps
        .builder()
        .value(password)
        .build()
    );
  }
}
