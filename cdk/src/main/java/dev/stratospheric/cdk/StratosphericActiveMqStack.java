package dev.stratospheric.cdk;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.amazonmq.CfnBroker;
import software.amazon.awscdk.services.ec2.CfnSecurityGroup;
import software.amazon.awscdk.services.ssm.StringParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StratosphericActiveMqStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;
  private final CfnBroker broker;
  private final CfnSecurityGroup activeMqSecurityGroup;
  private final String username;
  private final String password;

  public StratosphericActiveMqStack(
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

    Network.NetworkOutputParameters networkOutputParameters = Network.getOutputParametersFromParameterStore(this, applicationEnvironment.getEnvironmentName());

    activeMqSecurityGroup = CfnSecurityGroup.Builder.create(this, "activeMqSecurityGroup")
      .vpcId(networkOutputParameters.getVpcId())
      .groupDescription("Security Group for the Active MQ instance")
      .groupName(applicationEnvironment.prefix("activeMqSecurityGroup"))
      .build();

    List<User> userList = new ArrayList<>();
    userList.add(new User(
      username,
      password
    ));

    this.broker = CfnBroker.Builder
      .create(this, "broker")
      .brokerName(applicationEnvironment.prefix("stratospheric-message-broker"))
      .hostInstanceType("mq.t2.micro")
      .engineType("ACTIVEMQ")
      .engineVersion("5.15.14")
      .authenticationStrategy("SIMPLE")
      .users(userList)
      .publiclyAccessible(false)
      .autoMinorVersionUpgrade(true)
      .deploymentMode("SINGLE_INSTANCE")
      .securityGroups(Collections.singletonList(activeMqSecurityGroup.getAttrGroupId()))
      .build();

    createOutputParameters();
  }

  private static String getParameterSecurityGroup(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_SECURITY_GROUP_ID, createParameterName(applicationEnvironment, PARAMETER_SECURITY_GROUP_ID))
      .getStringValue();
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

  public ActiveMqOutputParameters getOutputParameters() {
    return new ActiveMqOutputParameters(
      this.activeMqSecurityGroup.getAttrGroupId(),
      this.username,
      this.password,
      this.broker.getAttrAmqpEndpoints().get(0),
      this.broker.getAttrStompEndpoints().get(0)
    );
  }

  public static ActiveMqOutputParameters getOutputParametersFromParameterStore(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return new ActiveMqOutputParameters(
      getParameterSecurityGroup(scope, applicationEnvironment),
      getParameterUsername(scope, applicationEnvironment),
      getParameterPassword(scope, applicationEnvironment),
      getParameterAmqpEndpoint(scope, applicationEnvironment),
      getParameterStompEndpoint(scope, applicationEnvironment));
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

  private static final String PARAMETER_SECURITY_GROUP_ID = "activeMqSecurityGroup";
  private static final String PARAMETER_USERNAME = "activeMqUsername";
  private static final String PARAMETER_PASSWORD = "activeMqPassword";
  private static final String PARAMETER_AMQP_ENDPOINT = "amqpEndpoint";
  private static final String PARAMETER_STOMP_ENDPOINT = "stompEndpoint";

  private void createOutputParameters() {

    StringParameter.Builder.create(this, PARAMETER_SECURITY_GROUP_ID)
      .parameterName(createParameterName(this.applicationEnvironment, PARAMETER_SECURITY_GROUP_ID))
      .stringValue(this.activeMqSecurityGroup.getAttrGroupId())
      .build();
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
  }

  private static String createParameterName(ApplicationEnvironment applicationEnvironment, String parameterName) {
    return applicationEnvironment.getEnvironmentName() + "-" + applicationEnvironment.getApplicationName() + "-ActiveMq-" + parameterName;
  }

  private static String getFailoverString(String url1, String url2) {
    if (url1 != null && url2 != null) {
      return "failover:(" + url1 + "," + url2 + ")";
    }

    if (url1 != null) {
      return url1;
    }

    return url2;
  }

  public static class ActiveMqOutputParameters {
    private final String activeMqSecurityGroup;
    private final String activeMqUsername;
    private final String activeMqPassword;
    private final String amqpEndpoint;
    private final String stompEndpoint;

    public ActiveMqOutputParameters(
      String activeMqSecurityGroup,
      String activeMqUsername,
      String activeMqPassword,
      String amqpEndpoint,
      String stompEndpoint
    ) {
      this.activeMqSecurityGroup = activeMqSecurityGroup;
      this.activeMqUsername = activeMqUsername;
      this.activeMqPassword = activeMqPassword;
      this.amqpEndpoint = amqpEndpoint;
      this.stompEndpoint = stompEndpoint;
    }

    public String getActiveMqSecurityGroup() {
      return activeMqSecurityGroup;
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
  }

  private static class User {

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
