# Getting Started with Spring Boot on AWS

[![Demo](s3simpleFileViewer.png)](https://stratospheric.dev)

## How to run the demo application

1. Make sure you have the AWS CLI installed and configured your `default` credentials and AWS region e.g. `eu-central-1` 
2. Ensure you have JDK 11 installed: `java -version`
3. Create the required infrastructure for the application:
```
cd cloudformation
./create.sh your-unique-bucket-name
```

Please note that for demonstration purposes the S3 Bucket and its content is publicly accessible. Remove it afterwards.

4. Create at least the following two parameters using the AWS Parameter Store (inside SSM):

```
/config/stratospheric-demo/custom.bucket-name -> your S3 bucket name
/config/stratospheric-demo/custom.sqs-queue-name -> stratospheric-demo-queue
```

The parameter values can be either `String` or `SecureString`

5. Run the application:
```
./gradlew bootRun
```
6. Upload the demo images to your S3 bucket
```
aws s3api put-object --bucket your-unique-bucket-name --key stratospheric-book.pdf --body stratospheric-book.pdf --acl public-read
aws s3api put-object --bucket your-unique-bucket-name --key stratospheric-book-cover.jpg --body stratospheric-book-cover.jpg --acl public-read
aws s3api put-object --bucket your-unique-bucket-name --key stratospheric-book-cover-mockup.jpg --body stratospheric-book-cover-mockup.jpg --acl public-read
```
7. Visit http://localhost:8080/ to open the file viewer. In addition to this, you should see incoming log messages from the SQS listener.
8. (Optional) Build and run the application inside a Docker Container
```
./gradlew assemble
docker build -t statospheric-demo .
docker run -p 8080:8080 -e AWS_REGION=eu-central-1 -e AWS_ACCESS_KEY_ID=XYZ -e AWS_SECRET_KEY=SECRET stratospheric-demo
```

## Further resources

- More information about the [Stratospheric project](https://stratospheric.dev)
- Get the E-Book Stratospheric on [Leanpub](https://leanpub.com/stratospheric)
- Spring Cloud AWS on [GitHub](https://github.com/spring-cloud/spring-cloud-aws)
- Spring Cloud AWS [documentation](https://docs.spring.io/spring-cloud-aws/docs/current/reference/html/)
- Maciej Walkowiak's [Spring Academy YouTube channel](https://www.youtube.com/channel/UCslYinLbZnzzUdG0BMaiDKw) with great content about Spring Cloud AWS

## Authors

- [Tom Hombergs](https://reflectoring.io/)
- [Björn Wilsmann](https://bjoernkw.com/)
- [Philip Riecks](https://rieckpil.de/)