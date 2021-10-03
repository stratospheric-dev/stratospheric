# How to deploy this AWS Lambda function

* Make sure your local AWS `default` profile has the credentials to deploy to our AWS account

```
./gradlew buildZip
npm install -g serverless
serverless deploy
```
