package dev.stratospheric.cdk;

import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.customresources.*;
import software.amazon.awscdk.services.cognito.*;
import software.amazon.awscdk.services.ssm.StringParameter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static software.amazon.awscdk.customresources.AwsCustomResourcePolicy.ANY_RESOURCE;

class StratosphericCognitoStack extends Stack {

  private final ApplicationEnvironment applicationEnvironment;

  private final UserPool userPool;
  private final UserPoolClient userPoolClient;
  private final UserPoolDomain userPoolDomain;
  private String userPoolClientSecret;

  public StratosphericCognitoStack(
    final Construct scope,
    final String id,
    final Environment awsEnvironment,
    final ApplicationEnvironment applicationEnvironment,
    final CognitoInputParameters inputParameters) {
    super(scope, id, StackProps.builder()
      .stackName(applicationEnvironment.prefix("Cognito"))
      .env(awsEnvironment).build());

    this.applicationEnvironment = applicationEnvironment;

    this.userPool = UserPool.Builder.create(this, "userPool")
      .userPoolName(inputParameters.authName + "-user-pool")
      .accountRecovery(AccountRecovery.EMAIL_ONLY)
      .passwordPolicy(PasswordPolicy.builder()
        .requireLowercase(true)
        .requireDigits(true)
        .requireSymbols(true)
        .requireUppercase(true)
        .tempPasswordValidity(Duration.days(7))
        .build())
      .build();

    this.userPoolClient = UserPoolClient.Builder.create(this, "userPoolClient")
      .userPoolClientName(inputParameters.authName + "-client")
      .generateSecret(true)
      .userPool(this.userPool)
      .oAuth(OAuthSettings.builder()
        .callbackUrls(Arrays.asList(
          String.format("%s/login/oauth2/code/cognito", inputParameters.externalUrl),
          "http://localhost:8080/login/oauth2/code/cognito"
        ))
        .flows(OAuthFlows.builder()
          .authorizationCodeGrant(true)
          .implicitCodeGrant(true)
          .build())
        .scopes(Arrays.asList(OAuthScope.EMAIL, OAuthScope.OPENID, OAuthScope.PROFILE))
        .build())
      .supportedIdentityProviders(Collections.singletonList(UserPoolClientIdentityProvider.COGNITO))
      .build();

    this.userPoolDomain = UserPoolDomain.Builder.create(this, "userPoolDomain")
      .userPool(this.userPool)
      .cognitoDomain(CognitoDomainOptions.builder()
        .domainPrefix("dev101")
        .build())
      .build();

    createOutputParameters(awsEnvironment);

    // commented out because the GitHub Actions user currently doesn't have this permission
//    applicationEnvironment.tag(this);
  }

  private static final String PARAMETER_USER_POOL_ID = "userPoolId";
  private static final String PARAMETER_USER_POOL_CLIENT_ID = "userPoolClientId";
  private static final String PARAMETER_USER_POOL_CLIENT_SECRET = "userPoolClientSecret";

  private void createOutputParameters(Environment awsEnvironment) {

    StringParameter userPoolId = StringParameter.Builder.create(this, "userPoolId")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_ID))
      .stringValue(this.userPool.getUserPoolId())
      .build();

    StringParameter userPoolClientId = StringParameter.Builder.create(this, "userPoolClientId")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_CLIENT_ID))
      .stringValue(this.userPoolClient.getUserPoolClientId())
      .build();

    // CloudFormation does not expose the UserPoolClient secret, so we can't access it directly with
    // CDK. As a workaround, we create a custom resource that calls the AWS API to get the secret, and
    // then store it in the parameter store like the other parameters.
    // Source: https://github.com/aws/aws-cdk/issues/7225

    AwsCustomResource describeUserPoolResource = AwsCustomResource.Builder.create(this, "describeUserPool")
      .resourceType("Custom::DescribeCognitoUserPoolClient")
      .onCreate(AwsSdkCall.builder()
        .region(awsEnvironment.getRegion())
        .service("CognitoIdentityServiceProvider")
        .action("describeUserPoolClient")
        .parameters(Map.of(
          "UserPoolId", this.userPool.getUserPoolId(),
          "ClientId", this.userPoolClient.getUserPoolClientId()
        ))
        .physicalResourceId(PhysicalResourceId.of(this.userPoolClient.getUserPoolClientId()))
        .build())
      .policy(AwsCustomResourcePolicy.fromSdkCalls(
        SdkCallsPolicyOptions.builder()
          .resources(ANY_RESOURCE)
          .build()
      ))
      .build();

    this.userPoolClientSecret = describeUserPoolResource.getResponseField("UserPoolClient.ClientSecret");
    StringParameter userPoolClientSecret = StringParameter.Builder.create(this, "userPoolClientSecret")
      .parameterName(createParameterName(applicationEnvironment, PARAMETER_USER_POOL_CLIENT_SECRET))
      .stringValue(this.userPoolClientSecret)
      .build();


  }

  @NotNull
  private static String createParameterName(ApplicationEnvironment applicationEnvironment, String parameterName) {
    return applicationEnvironment.getEnvironmentName() + "-" + applicationEnvironment.getApplicationName() + "-Cognito-" + parameterName;
  }

  public CognitoOutputParameters getOutputParameters() {
    return new CognitoOutputParameters(
      this.userPool.getUserPoolId(),
      this.userPoolClient.getUserPoolClientId(),
      userPoolClientSecret);
  }

  public static CognitoOutputParameters getOutputParametersFromParameterStore(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return new CognitoOutputParameters(
      getParameterUserPoolId(scope, applicationEnvironment),
      getParameterUserPoolClientId(scope, applicationEnvironment),
      getParameterUserPoolClientSecret(scope, applicationEnvironment));
  }

  private static String getParameterUserPoolId(Construct scope, ApplicationEnvironment applicationEnvironment) {
    return StringParameter.fromStringParameterName(scope, PARAMETER_USER_POOL_ID, createParameterName(applicationEnvironment, PARAMETER_USER_POOL_ID))
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
    private final String authName;
    private final String externalUrl;

    public CognitoInputParameters(String authName, String externalUrl) {
      this.authName = authName;
      this.externalUrl = externalUrl;
    }
  }

  public static class CognitoOutputParameters {
    private final String userPoolId;
    private final String userPoolClientId;
    private final String userPoolClientSecret;

    public CognitoOutputParameters(String userPoolId, String userPoolClientId, String userPoolClientSecret) {
      this.userPoolId = userPoolId;
      this.userPoolClientId = userPoolClientId;
      this.userPoolClientSecret = userPoolClientSecret;
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
  }

}
