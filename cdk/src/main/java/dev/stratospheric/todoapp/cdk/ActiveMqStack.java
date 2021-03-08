package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.amazonmq.CfnBroker;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ssm.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActiveMqStack extends Stack {

  private static final String PARAMETER_USERNAME = "activeMqUsername";
  private static final String PARAMETER_PASSWORD = "activeMqPassword";
  private static final String PARAMETER_AMQP_ENDPOINT = "amqpEndpoint";
  private static final String PARAMETER_STOMP_ENDPOINT = "stompEndpoint";
  private static final String PARAMETER_SECURITY_GROUP_ID = "activeMqSecurityGroupId";

  private final ApplicationEnvironment applicationEnvironment;
  private final CfnBroker broker;
  private final String username;
  private final String password;
  private final String securityGroupId;

  public ActiveMqStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final String username
  ) {
    super(
      scope,
      id,
      StackProps.builder()
        .stackName(applicationEnvironment.prefix("ActiveMq"))
        .env(awsEnvironment)
        .build()
    );

    this.applicationEnvironment = applicationEnvironment;
    this.username = username;
    this.password = generatePassword();

    List<User> userList = new ArrayList<>();
    userList.add(new User(
      username,
      password
    ));

    Network.NetworkOutputParameters networkOutputParameters = Network.getOutputParametersFromParameterStore(this, applicationEnvironment.getEnvironmentName());
    String vpcName = "NetworkStack/Network/vpc";

    IVpc vpc = Vpc.fromLookup(this,
      vpcName,
      VpcLookupOptions.builder()
        .vpcName(vpcName)
        .build()
    );

    SecurityGroup amqSecurityGroup = SecurityGroup.Builder
      .create(this, "amqSecurityGroup")
      .securityGroupName("AmazonMQSecurityGroup")
      .vpc(vpc)
      .build();
    this.securityGroupId = amqSecurityGroup.getSecurityGroupId();

    this.broker = CfnBroker.Builder
      .create(this, "amqBroker")
      .brokerName(applicationEnvironment.prefix("stratospheric-message-broker"))
      .securityGroups(Collections.singletonList(this.securityGroupId))
      .subnetIds(networkOutputParameters.getIsolatedSubnets())
      .hostInstanceType("mq.t2.micro")
      .engineType("ACTIVEMQ")
      .engineVersion("5.15.14")
      .authenticationStrategy("SIMPLE")
      .encryptionOptions(
        CfnBroker.EncryptionOptionsProperty
          .builder()
          .useAwsOwnedKey(true)
          .build()
      )
      .users(userList)
      .publiclyAccessible(false)
      .autoMinorVersionUpgrade(true)
      .deploymentMode("ACTIVE_STANDBY_MULTI_AZ")
      .build();

    System.out.println(this.broker.getAttrStompEndpoints());
    System.out.println("------ #");

    createOutputParameters();
  }

  public static ActiveMqOutputParameters getOutputParametersFromParameterStore(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return new ActiveMqOutputParameters(
      getParameterUsername(scope, applicationEnvironment),
      getParameterPassword(scope, applicationEnvironment),
      getParameterAmqpEndpoint(scope, applicationEnvironment),
      getParameterStompEndpoint(scope, applicationEnvironment),
      getParameterSecurityGroupId(scope, applicationEnvironment)
    );
  }

  private static String getParameterUsername(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_USERNAME, createParameterName(applicationEnvironment, PARAMETER_USERNAME))
      .getStringValue();
  }

  private static String getParameterPassword(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_PASSWORD, createParameterName(applicationEnvironment, PARAMETER_PASSWORD))
      .getStringValue();
  }

  private static String getParameterAmqpEndpoint(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_AMQP_ENDPOINT, createParameterName(applicationEnvironment, PARAMETER_AMQP_ENDPOINT))
      .getStringValue();
  }

  private static String getParameterStompEndpoint(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_STOMP_ENDPOINT, createParameterName(applicationEnvironment, PARAMETER_STOMP_ENDPOINT))
      .getStringValue();
  }

  private static String getParameterSecurityGroupId(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_SECURITY_GROUP_ID, createParameterName(applicationEnvironment, PARAMETER_SECURITY_GROUP_ID))
      .getStringValue();
  }

  private String generatePassword() {
    PasswordGenerator passwordGenerator = new PasswordGenerator();
    CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
    CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
    lowerCaseRule.setNumberOfCharacters(5);
    CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
    CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
    upperCaseRule.setNumberOfCharacters(5);
    CharacterData digitChars = EnglishCharacterData.Digit;
    CharacterRule digitRule = new CharacterRule(digitChars);
    digitRule.setNumberOfCharacters(5);
    return passwordGenerator.generatePassword(32, lowerCaseRule, upperCaseRule, digitRule);
  }

  private void createOutputParameters() {
    StringParameter.Builder.create(this, PARAMETER_USERNAME)
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USERNAME))
      .stringValue(username)
      .build();

    StringParameter.Builder.create(this, PARAMETER_PASSWORD)
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_PASSWORD))
      .stringValue(password)
      .build();

    StringParameter.Builder.create(this, PARAMETER_AMQP_ENDPOINT)
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_AMQP_ENDPOINT))
      .stringValue(Fn.select(0, this.broker.getAttrAmqpEndpoints()))
      .build();

    StringParameter.Builder.create(this, PARAMETER_STOMP_ENDPOINT)
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_STOMP_ENDPOINT))
      .stringValue(Fn.select(0, this.broker.getAttrStompEndpoints()))
      .build();

    StringParameter.Builder.create(this, PARAMETER_SECURITY_GROUP_ID)
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_SECURITY_GROUP_ID))
      .stringValue(this.securityGroupId)
      .build();
  }

  private static String createParameterName(ApplicationEnvironment applicationEnvironment, String parameterName) {
    return applicationEnvironment.getEnvironmentName() + "-" + applicationEnvironment.getApplicationName() + "-ActiveMq-" + parameterName;
  }

  public static class ActiveMqOutputParameters {
    private final String activeMqUsername;
    private final String activeMqPassword;
    private final String amqpEndpoint;
    private final String stompEndpoint;
    private final String activeMqSecurityGroupId;

    public ActiveMqOutputParameters(
      String activeMqUsername,
      String activeMqPassword,
      String amqpEndpoint,
      String stompEndpoint,
      String activeMqSecurityGroupId
    ) {
      this.activeMqUsername = activeMqUsername;
      this.activeMqPassword = activeMqPassword;
      this.amqpEndpoint = amqpEndpoint;
      this.stompEndpoint = stompEndpoint;
      this.activeMqSecurityGroupId = activeMqSecurityGroupId;
    }

    public String getAmqpEndpoint() {
      return amqpEndpoint;
    }

    public String getStompEndpoint() {
      return stompEndpoint;
    }

    public String getActiveMqUsername() {
      return activeMqUsername;
    }

    public String getActiveMqPassword() {
      return activeMqPassword;
    }

    public String getActiveMqSecurityGroupId() {
      return activeMqSecurityGroupId;
    }
  }

  static class User {

    String username;

    String password;

    public User() {
    }

    public User(String username, String password) {
      this.username = username;
      this.password = password;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }
}
