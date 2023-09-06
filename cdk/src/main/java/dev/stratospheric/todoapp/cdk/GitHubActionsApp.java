package dev.stratospheric.todoapp.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public class GitHubActionsApp {

  public static void main(final String[] args) {
    App app = new App();

    String environmentName = (String) app.getNode().tryGetContext("environmentName");
    Validations.requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

    String applicationName = (String) app.getNode().tryGetContext("applicationName");
    Validations.requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

    String accountId = (String) app.getNode().tryGetContext("accountId");
    Validations.requireNonEmpty(accountId, "context variable 'accountId' must not be null");

    String region = (String) app.getNode().tryGetContext("region");
    Validations.requireNonEmpty(region, "context variable 'region' must not be null");

    String gitHubActionsUserName = (String) app.getNode().tryGetContext("gitHubActionsUserName");
    Validations.requireNonEmpty(region, "context variable 'gitHubActionsUserName' must not be null");

    Environment awsEnvironment = makeEnv(accountId, region);

    ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
      applicationName,
      environmentName
    );

    String stackPrefix = "GitHubActions";
    String groupName = stackPrefix + "Group";

    var gitHubActionsStack = new Stack(app, "GitHubActionsStack", StackProps.builder()
      .stackName(applicationEnvironment.prefix(stackPrefix))
      .env(awsEnvironment)
      .build());

    var managedGroupPolicies = Stream.of(
      "AmazonEC2FullAccess",
      "AmazonECS_FullAccess",
      "AmazonCognitoPowerUser",
      "AWSCloudFormationFullAccess",
      "AWSLambda_FullAccess"
    )
      .map(ManagedPolicy::fromAwsManagedPolicyName)
      .toList();

    var gitHubActionsGroup = new Group(
      gitHubActionsStack,
      groupName,
      GroupProps.builder()
        .groupName(groupName)
        .managedPolicies(managedGroupPolicies)
        .build()
    );
    gitHubActionsGroup.addToPolicy(
      PolicyStatement.Builder.create()
        .sid("GetAuthorizationToken")
        .effect(Effect.ALLOW)
        .actions(Collections.singletonList("ecr:GetAuthorizationToken"))
        .resources(Collections.singletonList("*"))
        .build()
    );
    gitHubActionsGroup.addToPolicy(
      PolicyStatement.Builder.create()
        .sid("ListImagesInRepository")
        .effect(Effect.ALLOW)
        .actions(Collections.singletonList("ecr:ListImages"))
        .resources(Collections.singletonList("*"))
        .build()
    );
    gitHubActionsGroup.addToPolicy(
      PolicyStatement.Builder.create()
        .sid("ManageRepositoryContents")
        .effect(Effect.ALLOW)
        .actions(
          Arrays.asList(
            "ecr:BatchCheckLayerAvailability",
            "ecr:GetDownloadUrlForLayer",
            "ecr:GetRepositoryPolicy",
            "ecr:DescribeRepositories",
            "ecr:ListImages",
            "ecr:DescribeImages",
            "ecr:BatchGetImage",
            "ecr:InitiateLayerUpload",
            "ecr:UploadLayerPart",
            "ecr:CompleteLayerUpload",
            "ecr:PutImage"
          )
        )
        .resources(Collections.singletonList("*"))
        .build()
    );

    new User(
      gitHubActionsStack,
      "GitHubActionsUser",
      UserProps.builder()
        .userName(gitHubActionsUserName)
        .managedPolicies(managedGroupPolicies)
        .groups(Collections.singletonList(gitHubActionsGroup))
        .build()
    );

    app.synth();
  }

  static Environment makeEnv(String account, String region) {
    return Environment.builder()
      .account(account)
      .region(region)
      .build();
  }
}
