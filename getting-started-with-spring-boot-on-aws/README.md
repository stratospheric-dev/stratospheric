# Demo Application for Blog Post on AWS

Title: Getting started with Spring Boot on AWS

## Intro Section


## Blog Post

Create the required infrastructure for the application:

```
cloudformation/create.sh
```

Build the application and run it inside a Docker Container:

```
./gradlew assemble
docker build -t statospheric-demo .
docker run -p 8080:8080 -e AWS_REGION=eu-central-1 -e AWS_ACCESS_KEY_ID=XYZ -e AWS_SECRET_KEY=SECRET stratospheric-demo



```

```
aws s3api put-object --bucket stratospheric-demo-bucket --key stratospheric-book.pdf --body stratospheric-book.pdf --profile stratospheric
aws s3api put-object --bucket stratospheric-demo-bucket --key stratospheric-book-cover.jpg --body stratospheric-book-cover.jpg --profile stratospheric
aws s3api put-object --bucket stratospheric-demo-bucket --key stratospheric-book-cover-mockup.jpg --body stratospheric-book-cover-mockup.jpg --profile stratospheric
```


## S3 Event Notification Feature:

> It is also possible to receive AWS generated event messages with the SQS message listeners. Because AWS messages does not contain the mime-type header, the Jackson message converter has to be configured with the strictContentTypeMatch property false to also parse message without the proper mime type.

## Conclusion

- Spring Cloud AWS makes AWS a first-citizen cloud provider
- Almost no effort to integrate the core AWS services
- A great roadmap for the Spring Cloud AWS (version 3.0) project ahead (new core maintainers, thanks to Maciej and Eddú btw!)

## Further resources

- link to Spring Cloud AWS
- link to Maciej's Spring Academy YT: https://www.youtube.com/channel/UCslYinLbZnzzUdG0BMaiDKw
- link to the GitHub repository of the app:
- link to the GitHub repository of Spring Cloud AWS: https://github.com/spring-cloud/spring-cloud-aws