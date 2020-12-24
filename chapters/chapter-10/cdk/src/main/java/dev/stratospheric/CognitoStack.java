package dev.stratospheric;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.cognito.*;

import java.util.List;
import java.util.Map;

public class CognitoStack extends Stack {

  public CognitoStack(final Construct scope,
                      final String id,
                      final Environment environment,
                      final Map<String, List<String>> clients) {

    super(scope, id, StackProps.builder()
      .stackName("CognitoCdkTestStack")
      .env(environment)
      .build());

    UserPool userPool = new UserPool(this, "stratospheric-cdk-user-pool", UserPoolProps
      .builder()
      .userPoolName("stratospheric-cdk-user-pool")
      .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
      .signInAliases(SignInAliases.builder().username(true).email(true).build())
      .standardAttributes(StandardAttributes.builder()
        .email(StandardAttribute.builder().mutable(false).required(true).build()).build())
      .signInCaseSensitive(true)
      .mfa(Mfa.OFF)
      .accountRecovery(AccountRecovery.EMAIL_ONLY)
      .passwordPolicy(PasswordPolicy.builder()
        .minLength(12)
        .requireLowercase(true)
        .requireUppercase(true)
        .requireDigits(true)
        .requireSymbols(true)
        .tempPasswordValidity(Duration.days(7))
        .build())
      .build());

    userPool.addDomain("CognitoDomain", UserPoolDomainOptions
      .builder()
      .cognitoDomain(CognitoDomainOptions.builder().domainPrefix("stratospheric-dev").build())
      .build());

    clients.forEach((key, value) -> {

      UserPoolClient userPoolClient = userPool.addClient(key, UserPoolClientOptions
        .builder()
        .userPoolClientName(key)
        .generateSecret(true)
        .oAuth(OAuthSettings
          .builder()
          .flows(OAuthFlows.builder().authorizationCodeGrant(true).build())
          .scopes(List.of(OAuthScope.OPENID, OAuthScope.EMAIL, OAuthScope.PROFILE))
          .callbackUrls(value)
          .build())
        .build());

      new CfnOutput(this, "CognitoUserPoolClient" + key + "Id", CfnOutputProps.builder()
        .value(userPoolClient.getUserPoolClientId())
        .build());
      System.out.println(userPoolClient.getUserPoolClientId());

    });

    new CfnOutput(this, "CognitoUserPoolId", CfnOutputProps.builder()
      .value(userPool.getUserPoolId())
      .build());

    new CfnOutput(this, "CognitoUserPoolProviderUrl", CfnOutputProps.builder()
      .value(userPool.getUserPoolProviderUrl())
      .build());

  }
}
