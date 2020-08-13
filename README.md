# AWS Topics

Verifying #22

## Idee

* wir schreiben jeder in unserem eigenen Blog koordiniert ueber AWS Themen
* wir reviewen gegenseitig
* die Code Beispiele fuegen wir einer lauffaehigen Beispielanwendung auf GitHub hinzu
* am Ende verpacken wir die Blog Posts in einem Guide/Buch/Lead Magnet/...

* Roter Faden: From 0 to 100: Get Your Software into AWS
* Idealerweise arbeiten wir alle an derselben Beispielanwendung waehrend wir die Blog Posts schreiben
* Zielgruppe: Java Entwickler mit Spring Boot Erfahrung

## Ideen fuer Themen

* Amplify
  * file system caching gotchas (e.g. with PWAs and Web Workers)
* PubSub with AWS IoT
* Cognito + Spring Security
* Deployment / CloudFormation / CI/CD
* Spring / Spring Cloud AWS
* AWS Java SDK v1 vs. v2
* Serverless / AWS Lambda (with/without Spring Cloud Functions)
* Caching / ElastiCache
* RDS
* DynamoDB
* AWS Database Migration Service
* DB Backups
* AutoScaling
* Monitoring / Logging
* Simple Email Service (SES)
* Privacy
* S3
* SQS & / vs. Amazon MQ (with JMS adapter/ AWS SDK)
* SNS (notifications)
* Amazon Elasticsearch Service
* AWS AppFlow (low code)
* AWS Cost Explorer
* AWS Data Pipeline (& Spring Batch)
* AWS Batch (& Spring Batch)
* AWS Cloud9
* leaky abstractions
* SCM security / preventing secrets and credentials from leaking into source code repositories

## Ideen fuer die Beispielanwendung

* CMS
* TODO App (Top-Kandidat)

## Chronology
* Intro (What? Why?)
  * goal: introduce the reader to why this book exist and what to expect
* The Example Application
  * goal: get to know the domain of the example application

### Getting Started
* Installing AWS CLI
* ...
* introduce some common AWS acronyms, and define which of these options are being covered in the book
  * Beanstalk
  * ECS + ECR
  * Custom EC2,
  * EKS
  * CloudFormation
  * ... more?
* Hello World App with AWS Console
* Deploy Hello World with CloudFormation

### The Example Application
* goal: the reader understands what to build (no code, just concept)
  * data model
  * screen shots
  * architecture / context diagram
  * technologies (Spring Boot, Thymeleaf, PostgreSQL, ...)
  * ...
* Feature Ideas:

### Deploying the App to AWS (Tom)
* Deployment Options in AWS
  * goal: give an idea of which options are available, 
* Recipe: Publishing a Docker Image to ECR
* Recipe: Deploying a Docker Image to ECS
* Recipe: Implementing a Continuous Delivery Pipeline

### Login (Lambda, S3, Thumbnail creator, Cognito + Spring Security) (Philip)
* Explain the Feature...
* Recipe: ...

### Create Todos (RDS) (Tom)
* 

### Send Todos to friend (SES, SQS) (Bjoern)
* 

### Push Notifications (PubSub with AWS IoT / SNS?) (Bjoern)
*

### Deploy with GitHub Actions (CI/CD + CloudFormation) (Tom)
* 

### Notes (store schemaless notes in DynamoDB) (Philip)
*
  
### Scaling (TODO)
* Recipe: Implementing Load Balancing
* Recipe: Caching with ElastiCache
* Recipe: AutoScaling the App

### Observability (TODO)
* Observability options in AWS
  * CloudWatch logs
  * CloudWatch metrics
  * 3rd party providers?
* Recipe: Sending Logs to CloudWatch
* Recipe: Sending Metrics to CloudWatch
* Cost Explorer?!?
  * Alarms?

### Side notes
* Managing organizations
  * Enabling the admin role for invited users
    * https://docs.aws.amazon.com/organizations/latest/userguide/orgs_manage_accounts_access.html
  * Enforcing MFA
    * https://docs.aws.amazon.com/IAM/latest/UserGuide/tutorial_users-self-manage-mfa-and-creds.html
    * https://docs.aws.amazon.com/IAM/latest/UserGuide/reference_policies_examples_aws_my-sec-creds-self-manage.html
    