package dev.stratospheric;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.amazonmq.CfnBroker;
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
    String password = passwordGenerator.generatePassword(32, lowerCaseRule, upperCaseRule, digitRule);

    List<User> userList = new ArrayList<>();
    userList.add(new User(
      username,
      password
    ));


    String name = "stratospheric-message-broker";
    CfnBroker cfnBroker = CfnBroker.Builder
      .create(
        this,
        name
      )
      .brokerName(name)
      .hostInstanceType("mq.t2.micro")
      .engineType("ACTIVEMQ")
      .engineVersion("5.15.14")
      .authenticationStrategy("SIMPLE")
      .users(userList)
      .publiclyAccessible(true)
      .autoMinorVersionUpgrade(true)
      .deploymentMode("ACTIVE_STANDBY_MULTI_AZ")
      .build();

    new CfnOutput(this,
      "ActiveMQEndpoint1",
      CfnOutputProps
        .builder()
        .value(cfnBroker.getAttrAmqpEndpoints().get(0))
        .build()
    );

    new CfnOutput(this,
      "ActiveMQStompEndpoint1",
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
