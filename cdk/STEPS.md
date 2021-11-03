# Deploying the Stratospheric Sample Application to your AWS Account

Prerequisites:

- You have a custom domain (e.g., `mycompany.io`) hosted within Amazon Route53. You can also host your domain at a
  different provider (e.g., GoDaddy, Namecheap, Hetzner, etc.). However, this involves additional manual effort to
  correctly set up SSL.
- You've created an SSL certificate within the AWS Certificate Manager for that domain and have the `ARN` for the SSL
  certificate.
- You've [configured a named profile](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-profiles.html) for
  the AWS CLI (e.g., `stratospheric`) with sufficient privileges to create / delete resources.
- Your Docker Engine is up and running.
- You have Node >= 16 installed: `node -v`
- You have Java 11 installed: `java -version`
- You're using an x64 processor or you're able to create a Docker image for this architecture (see
  this [article](https://blog.jaimyn.dev/how-to-build-multi-architecture-docker-images-on-an-m1-mac/) if you're using an
  Apple M1).

Bootstrapping the entire infrastructure from scratch takes 20 - 30 minutes. You can speed up the process by increasing
the instances sizes for the database, ActiveMQ and the ECS tasks.

**IMPORTANT NOTE**: Deploying this infrastructure will result in recurring costs if you don't clean up the resources
afterward. Closely follow the progress of the stack deletion and check the CloudFormation overview in the AWS console
afterward. There shouldn't be any stack definitions left.

## 1. Deploy the Surrounding Infrastructure

We're assuming you're using a named profile for the AWS CLI called `stratospheric`. If you're using the default profile,
you can remove `-- --profile statrospheric` from all the following commands:

1. Navigate to the `cdk` folder: `cd cdk`
2. Adjust the configuration in `cdk.json`:
1. `applicationName`: The name of your application, e.g., `todo-app` (make sure the application name and staging
   combination is unique as we're creating resources that require a unique name).
2. `region`: The region you want to deploy the infrastructure to, e.g., `eu-central-1`.
3. `accountId`: The account ID of your AWS account, e.g., `221875718260`.
4. `dockerRepositoryName`: The name of your Docker repository. Unless you want to deploy a Docker image from another
   registry, this should be equal to the `applicationName`, e.g., `todo-app`.
5. `dockerImageTag`: The Docker image you want to deploy. Use `1` and update this number if you want to deploy a new
   version of the application.
6. `applicationUrl`: The full application URL of your application, e.g., `https://app.stratospheric.dev`.
7. `loginPageDomainPrefix`: This becomes the subdomain for the Cognito login form, e.g., `stratospheric-staging`.
8. `environmentName`: The application environment, e.g. ,`staging` or `prod`.
9. `springProfile`: The Spring profile that should be activated for the running ECS container, e.g., `aws`.
10. `activeMqUsername`: The name of the ActiveMQ root user, e.g., `activemqUser`.
11. (optional - leave empty if you're not planning to deploy the Canary stack) `canaryUsername`: `canary`,
12. (optional - leave empty if you're not planning to deploy the Canary
    stack) `canaryUserPassword`: `SECRET_OVERRIDDEN_BY_WORKFLOW`.
13. (optional - leave empty if you're not planning to deploy the monitoring stack) `confirmationEmail`: The email for
    receiving CloudWatch alerts, e.g.,`info@stratospheric.dev`.
14. `applicationDomain`: The domain of your application, without protocol, e.g., `app.stratospheric.dev`.
15. `sslCertificateArn`: The `ARN` for the SSL certificate for your custom domain,
    e.g., `arn:aws:acm:eu-central-1:221875718260:certificate/8d92169c-ea74-4086-b407-b951429ac2b1`,
16. `hostedZoneDomain`: The domain name for the hosted zone within Route53, e.g., `stratospheric.dev`.
17. (optional - leave empty if you're not planning to deploy the deployment sequencer stack) `githubToken`: An access
    token for GitHub to trigger GitHub Actions remotely from the AWS Lambda function.
3. Bootstrap CDK for your AWS account:

```
npm run bootstrap -- --profile stratospheric
```

3. Deploy the `NetworkStack`-dependent infrastructure:

```
npm run network:deploy -- --profile stratospheric
npm run database:deploy -- --profile stratospheric
npm run activeMq:deploy -- --profile stratospheric
```

4. (or in parallel to #3) Deploy `NetworkStack`-independent infrastructure:

```
npm run repository:deploy -- --profile stratospheric
npm run messaging:deploy -- --profile stratospheric
npm run cognito:deploy -- --profile stratospheric
```

## 2. Build and Push the First Docker Image

Build the first Docker image:

```
cd application
./gradlew build

docker build -t <accountId>.dkr.ecr.<region>.amazonaws.com/<applicationName>:1 .

aws ecr get-login-password --region <region> --profile stratospheric | docker login --username AWS --password-stdin <accountId>.dkr.ecr.<region>.amazonaws.com

docker push <accountId>.dkr.ecr.<region>.amazonaws.com/<applicationName>:1
```

On Apple M1:

```shell
docker buildx build --platform linux/amd64,linux/arm64 --push -t <accountId>.dkr.ecr.<region>.amazonaws.com/todo-app:1 .
```

## 3. Deploy the Docker Image to the ECS Cluster

1. Customize the `dockerImageTag` property inside the `cdk/cdk.json` file to match the Docker image tag you've just
   pushed:

```shell
npm run service:deploy -- --profile stratospheric
```

## 4. Secure the Application with SSL

```
npm run domain:deploy -- --profile rieckpil
```

Afterwards, you'll be able to access the application from your custom domain.

Please consider the following:

- The sharing functionality only works if either you:
  - request production access for SES and verify the domain from which your application sends emails
  - manually verify all addresses you're about to send emails to and also verify the domain from which your application
    sends emails

## 5. (Optional): Deploy the Monitoring Infrastructure

1. Deploying the Amazon CloudWatch dashboard and alarms:

```
cd cdk
npm run monitoring:deploy -- --profile stratospheric
```

2. You'll receive an email to verify the SNS subscription based on what you've configured for `confirmationEmail`.

## 6. (Optional): Deploy the Canary Stack

1. Create an application user within the sample application.
2. Update the `canaryUsername` and `canaryUserPassword` inside the `cdk/cdk.json` file.
3. Deploy the canary stack.

```
cd cdk
npm run canary:deploy -- --profile stratospheric
```

## 7. Destroy Everything

Run all `npm run *:destroy -- --profile stratospheric` scripts in the reverse order the resource were created in.

Visit the CloudFormation web console to ensure all stacks have been removed.
