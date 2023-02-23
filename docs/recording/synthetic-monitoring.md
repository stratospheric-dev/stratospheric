# Synthetic Monitoring with Amazon CloudWatch

## What is Synthetic Monitoring?

Synthetic monitoring, also known as active monitoring or proactive monitoring, is a technique used to simulate user interactions with an application or system in order to detect and prevent issues before they affect real users. It involves setting up automated scripts that mimic typical user behavior and running them at regular intervals from various locations around the world.

Synthetic monitoring is important because it allows organizations to proactively detect and fix issues before they impact real users. By simulating user behavior, synthetic monitoring can identify performance issues, error messages, and other problems that may not be apparent from simple uptime monitoring. Additionally, synthetic monitoring can help organizations identify and address issues with third-party services, such as remote APIs (e.g. the contract changed without notice, the SSL certificate expired, we're running into limits or the budget is taken), that can impact application performance.

Overall, synthetic monitoring provides a comprehensive view of application and system performance, helping organizations to improve user experience, minimize downtime, and ultimately increase customer satisfaction.

Helpful technique to identify issue before a customer reports a failure. Continuously run important operations on production.

## What does AWS Offer?

Amazon CloudWatch canaries are a feature of Amazon CloudWatch that allows you to monitor your applications and infrastructure using synthetic transactions. The purpose of CloudWatch canaries is to simulate user behavior and monitor application health, so you can proactively detect issues before they affect real users.

The term “canary testing” comes from the rather morbid practice of taking canary birds down into mines to act as an early warning of poisonous and odorless gases. Due to their faster oxygen consumption, the birds would drop dead before any human would notice a change.

CloudWatch canaries work by creating scripts that simulate user interactions with your application. These scripts can be customized to perform specific actions, such as clicking buttons, filling out forms, or navigating to different pages. You can schedule these canaries to run at regular intervals to monitor your application's availability, responsiveness, and functionality.

CloudWatch canaries can also be used to monitor third-party services that your application depends on, such as databases, APIs, or content delivery networks. By simulating user interactions with these services, you can detect performance issues or errors that may impact your application's performance.

Overall, the purpose of CloudWatch canaries is to provide an automated and proactive monitoring solution that can detect and diagnose issues in your application or infrastructure. By using CloudWatch canaries, you can improve application reliability, reduce downtime, and ultimately deliver a better user experience.

AWS solution to it: Synthetic monitoring within Amazon CloudWatch
- Canaries are scripts written in Node.js or Python. They create Lambda functions in your account that use Node.js or Python as a framework. Canaries work over both HTTP and HTTPS protocols.
- those runtimes get depracted over time, see https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CloudWatch_Synthetics_Canaries_Library.html. You get an email 60 days before the deprecation
- Think of it as a scheduled cron job that performs verification
- Use cases: hearbeat monitoring, broken link checker, visual monitoring blueprint (compare screenshots to a baseline)
- Canaries offer programmatic access to a headless Google Chrome Browser via Puppeteer or Selenium Webdriver.
- Canaries can run as often as once per minute. You can use both cron and rate expressions to schedule canaries.

## What are going to build?

- consider main use cases, think of not polluting data in the system
- checkout system: try with a test customer or a test credit card
- define personas and typical user interaction within your system
- test entire transactions
- robust
- store sensitive information outside of the code

# Creating the Canary

## Explain setup

- Require a sample user
- Could be improved and completely automated but for now requires the user to be present
- First deploy the application and then create the technical user and store the password in a secret store (e.g. GitHub Secrets or the AWS Secrets Manager or Parameter Store)
- L2 CDK construct is still in Alpha (https://docs.aws.amazon.com/cdk/api/v2/docs/aws-synthetics-alpha-readme.html), we're falling back to L1 and CloudFormation constructs

## Include in our pipeline

- Include an alarm to get notified when the canary fails

## View results

- Show objects in bucket
- Show results
