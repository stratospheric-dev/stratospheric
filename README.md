# AWS Topics

## Idee

* wir schreiben jeder in unserem eigenen Blog koordiniert ueber AWS Themen
* wir reviewen gegenseitig
* die Code Beispiele fuegen wir einer lauffaehigen Beispielanwendung auf GitHub hinzu
* am Ende verpacken wir die Blog Posts in einem Guide/Buch/Lead Magnet/...

* Roter Faden: From 0 to 100: Get Your Software into AWS
* Idealerweise arbeiten wir alle an derselben Beispielanwendung waehrend wir die Blog Posts schreiben

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
* Tom's Blogtrack App?!? Haette den Vorteil, dass es eine echte App ist, die auch wirklich live geht!

## Chronology
* Intro (What? Why?)
  * goal: introduce the reader to why this book exist and what to expect
* The Example Application
  * goal: get to know the domain of the example application

### Deploying the App to AWS
* Deployment Options in AWS
  * goal: give an idea of which options are available, introduce some common AWS acronyms, and define which of these options are being covered in the book
  * Beanstalk
  * ECS + ECR
  * Custom EC2,
  * EKS
  * ... more?
* Recipe: Publishing a Docker Image to ECR
* Recipe: Deploying a Docker Image to ECS
* Recipe: Implementing a Continuous Delivery Pipeline

### Scaling
* Recipe: Implementing Load Balancing

* Recipe: AutoScaling the App

### Observability
* Observability options in AWS
  * CloudWatch logs
  * CloudWatch metrics
  * 3rd party providers?
* Recipe: Sending Logs to CloudWatch
* Recipe: Sending Metrics to CloudWatch

### Connecting Common Services

* Recipe: Connecting the App to a relational database (RDS)
* Recipe: Connecting the App to SQS
* Recipe: Connecting the App to S3
* Recipe: Sending emails with SES
* Recipe: Connecting the App to DynamoDB
