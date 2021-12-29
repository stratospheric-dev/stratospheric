package dev.stratospheric.todoapp.cdk;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.customresources.SdkCallsPolicyOptions;
import software.amazon.awscdk.services.cognito.AccountRecovery;
import software.amazon.awscdk.services.cognito.AutoVerifiedAttrs;
import software.amazon.awscdk.services.cognito.CognitoDomainOptions;
import software.amazon.awscdk.services.cognito.Mfa;
import software.amazon.awscdk.services.cognito.OAuthFlows;
import software.amazon.awscdk.services.cognito.OAuthScope;
import software.amazon.awscdk.services.cognito.OAuthSettings;
import software.amazon.awscdk.services.cognito.PasswordPolicy;
import software.amazon.awscdk.services.cognito.SignInAliases;
import software.amazon.awscdk.services.cognito.StandardAttribute;
import software.amazon.awscdk.services.cognito.StandardAttributes;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.UserPoolClientIdentityProvider;
import software.amazon.awscdk.services.cognito.UserPoolDomain;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

import static software.amazon.awscdk.customresources.AwsCustomResourcePolicy.ANY_RESOURCE;

class CognitoStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;

  private final UserPool userPool;
  private final UserPoolClient userPoolClient;
  private final UserPoolDomain userPoolDomain;
  private String userPoolClientSecret;
  private final String logoutUrl;

  public CognitoStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final CognitoInputParameters inputParameters) {
    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Cognito"))
      .env(awsEnvironment).build());

    this.applicationEnvironment = applicationEnvironment;
    this.logoutUrl = String.format("https://%s.auth.%s.amazoncognito.com/logout", inputParameters.loginPageDomainPrefix, awsEnvironment.getRegion());

    this.userPool = UserPool.Builder.create(this, "userPool")
      .userPoolName(inputParameters.applicationName + "-user-pool")
      .selfSignUpEnabled(false)
      .accountRecovery(AccountRecovery.EMAIL_ONLY)
      .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
      .signInAliases(SignInAliases.builder().username(true).email(true).build())
      .signInCaseSensitive(true)
      .standardAttributes(StandardAttributes.builder()
        .email(StandardAttribute.builder().required(true).mutable(false).build())
        .build())
      .mfa(Mfa.OFF)
      .passwordPolicy(PasswordPolicy.builder()
        .requireLowercase(true)
        .requireDigits(true)
        .requireSymbols(true)
        .requireUppercase(true)
        .minLength(12)
        .tempPasswordValidity(Duration.days(7))
        .build())
      .build();

    this.userPoolClient = UserPoolClient.Builder.create(this, "userPoolClient")
      .userPoolClientName(inputParameters.applicationName + "-client")
      .generateSecret(true)
      .userPool(this.userPool)
      .oAuth(OAuthSettings.builder()
        .callbackUrls(Arrays.asList(
          String.format("%s/login/oauth2/code/cognito", inputParameters.applicationUrl),
          "http://localhost:8080/login/oauth2/code/cognito"
        ))
        .logoutUrls(Arrays.asList(inputParameters.applicationUrl, "http://localhost:8080"))
        .flows(OAuthFlows.builder()
          .authorizationCodeGrant(true)
          .build())
        .scopes(Arrays.asList(OAuthScope.EMAIL, OAuthScope.OPENID, OAuthScope.PROFILE))
        .build())
      .supportedIdentityProviders(Collections.singletonList(UserPoolClientIdentityProvider.COGNITO))
      .build();

    this.userPoolDomain = UserPoolDomain.Builder.create(this, "userPoolDomain")
      .userPool(this.userPool)
      .cognitoDomain(CognitoDomainOptions.builder()
        .domainPrefix(inputParameters.loginPageDomainPrefix)
        .build())
      .build();

    createOutputParameters(awsEnvironment);

    applicationEnvironment.tag(this);
  }

  private static final String PARAMETER_USER_POOL_ID = "userPoolId";
  private static final String PARAMETER_USER_POOL_CLIENT_ID = "userPoolClientId";
  private static final String PARAMETER_USER_POOL_CLIENT_SECRET = "userPoolClientSecret";
  private static final String PARAMETER_USER_POOL_LOGOUT_URL = "userPoolLogoutUrl";
  private static final String PARAMETER_USER_POOL_PROVIDER_URL = "userPoolProviderUrl";

  private void createOutputParameters(Environment awsEnvironment) {

    StringParameter.Builder.create(this, "userPoolId")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_ID))
      .stringValue(this.userPool.getUserPoolId())
      .build();

    StringParameter.Builder.create(this, "userPoolClientId")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_CLIENT_ID))
      .stringValue(this.userPoolClient.getUserPoolClientId())
      .build();

    StringParameter.Builder.create(this, "logoutUrl")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_LOGOUT_URL))
      .stringValue(this.logoutUrl)
      .build();

    StringParameter.Builder.create(this, "providerUrl")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_PROVIDER_URL))
      .stringValue(this.userPool.getUserPoolProviderUrl())
      .build();

    // CloudFormation does not expose the UserPoolClient secret, so we can't access it directly with
    // CDK. As a workaround, we create a custom resource that calls the AWS API to get the secret, and
    // then store it in the parameter store like the other parameters.
    // Source: https://github.com/aws/aws-cdk/issues/7225

    AwsSdkCall fetchUserPoolClientMetadata = AwsSdkCall.builder()
      .region(awsEnvironment.getRegion())
      .service("CognitoIdentityServiceProvider")
      .action("describeUserPoolClient")
      .parameters(Map.of(
        "UserPoolId", this.userPool.getUserPoolId(),
        "ClientId", this.userPoolClient.getUserPoolClientId()
      ))
      .physicalResourceId(PhysicalResourceId.of(this.userPoolClient.getUserPoolClientId()))
      .build();

    AwsCustomResource describeUserPoolResource = AwsCustomResource.Builder.create(this, "describeUserPool")
      .resourceType("Custom::DescribeCognitoUserPoolClient")
      .installLatestAwsSdk(false)
      .onUpdate(fetchUserPoolClientMetadata)
      .onCreate(fetchUserPoolClientMetadata)
      .policy(AwsCustomResourcePolicy.fromSdkCalls(
        SdkCallsPolicyOptions.builder()
          .resources(ANY_RESOURCE)
          .build()
      ))
      .build();

    this.userPoolClientSecret = describeUserPoolResource.getResponseField("UserPoolClient.ClientSecret");

    StringParameter.Builder.create(this, "userPoolClientSecret")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_CLIENT_SECRET))
      .stringValue(this.userPoolClientSecret)
      .build();
  }

  private static String createParameterName(ApplicationEnvironment applicationEnvironment, String parameterName) {
    return applicationEnvironment.getEnvironmentName() + "-" + applicationEnvironment.getApplicationName() + "-Cognito-" + parameterName;
  }

  public CognitoOutputParameters getOutputParameters() {
    return new CognitoOutputParameters(
      this.userPool.getUserPoolId(),
      this.userPoolClient.getUserPoolClientId(),
      userPoolClientSecret,
      this.logoutUrl,
      this.userPool.getUserPoolProviderUrl());
  }

  public static CognitoOutputParameters getOutputParametersFromParameterStore(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return new CognitoOutputParameters(
      getParameterUserPoolId(scope, applicationEnvironment),
      getParameterUserPoolClientId(scope, applicationEnvironment),
      getParameterUserPoolClientSecret(scope, applicationEnvironment),
      getParameterLogoutUrl(scope, applicationEnvironment),
      getParameterUserPoolProviderUrl(scope, applicationEnvironment));
  }

  private static String getParameterUserPoolId(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_USER_POOL_ID, createParameterName(applicationEnvironment, PARAMETER_USER_POOL_ID))
      .getStringValue();
  }

  private static String getParameterLogoutUrl(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_USER_POOL_LOGOUT_URL, createParameterName(applicationEnvironment, PARAMETER_USER_POOL_LOGOUT_URL))
      .getStringValue();
  }

  private static String getParameterUserPoolProviderUrl(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_USER_POOL_PROVIDER_URL, createParameterName(applicationEnvironment, PARAMETER_USER_POOL_PROVIDER_URL))
      .getStringValue();
  }

  private static String getParameterUserPoolClientId(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_USER_POOL_CLIENT_ID, createParameterName(applicationEnvironment, PARAMETER_USER_POOL_CLIENT_ID))
      .getStringValue();
  }

  private static String getParameterUserPoolClientSecret(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_USER_POOL_CLIENT_SECRET, createParameterName(applicationEnvironment, PARAMETER_USER_POOL_CLIENT_SECRET))
      .getStringValue();
  }

  public static class CognitoInputParameters {
    private final String applicationName;
    private final String applicationUrl;
    private final String loginPageDomainPrefix;

    public CognitoInputParameters(String applicationName, String applicationUrl, String loginPageDomainPrefix) {
      this.applicationName = applicationName;
      this.applicationUrl = applicationUrl;
      this.loginPageDomainPrefix = loginPageDomainPrefix;
    }
  }

  public static class CognitoOutputParameters {
    private final String userPoolId;
    private final String userPoolClientId;
    private final String userPoolClientSecret;
    private final String logoutUrl;
    private final String providerUrl;

    public CognitoOutputParameters(
      String userPoolId,
      String userPoolClientId,
      String userPoolClientSecret,
      String logoutUrl,
      String providerUrl) {
      this.userPoolId = userPoolId;
      this.userPoolClientId = userPoolClientId;
      this.userPoolClientSecret = userPoolClientSecret;
      this.logoutUrl = logoutUrl;
      this.providerUrl = providerUrl;
    }

    public String getUserPoolId() {
      return userPoolId;
    }

    public String getUserPoolClientId() {
      return userPoolClientId;
    }

    public String getUserPoolClientSecret() {
      return userPoolClientSecret;
    }

    public String getLogoutUrl() {
      return logoutUrl;
    }

    public String getProviderUrl() {
      return providerUrl;
    }
  }

}
