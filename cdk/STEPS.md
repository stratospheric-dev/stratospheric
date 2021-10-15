
Prerequisites:

- a hosted domain inside Amazon Route53
- the `arn` for the SSL certificate for that domain
- AWS credentials configured with sufficient rights to create/delete resources
- expected initial bootstrap duration 30 - 45 minutes (you can speed up the process by increasing the instances sizes for the DB, ActiveMQ and the ECS cluster)
- Docker Engine and an x64 processor

**IMPORTANT NOTE**: Deploying this infrastructure will result in reoccuring costs if you don't cleanup the resources afterward. Closely follow

## 1. Deploy the surrounding Infrastructure

```
npm run bootstrap -- --profile rieckpil
npm run network:deploy -- --profile rieckpil
npm run repository:deploy -- --profile rieckpil
npm run cognito:deploy -- --profile rieckpil
npm run database:deploy -- --profile rieckpil
npm run messaging:deploy -- --profile rieckpil
npm run activeMq:deploy -- --profile rieckpil

```

## 2. Build and push the first Docker Image

Build the first Docker Image

```
./gradlew build
docker build -t todo-app .
docker tag todo-app 547530709389.dkr.ecr.eu-central-1.amazonaws.com/todo-app:1
docker tag todo-app ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/todo-app:1

aws ecr get-login-password --region eu-central-1 --profile rieckpil | docker login --username AWS --password-stdin 547530709389.dkr.ecr.eu-central-1.amazonaws.com

docker push 547530709389.dkr.ecr.eu-central-1.amazonaws.com/todo-app:1
```

## 3. Deploy the Docker Image to the ECS Cluster

```shell
npm run service:deploy -- --profile rieckpil
```

## 4. Secure the access with SSL

```
npm run domain:deploy -- --profile rieckpil
```

Things to consider:
- For Sending Emails with SES you need to get out of the sandbox mode (see the book) hence the sharing feature won't work before. This takes some hours as you have to raise a support ticket with the AWS Support. Alternative manually verify each email youÄre about to send an invitation to

## 5. Deploy Optional Infrastructure
Optional

```
# After App Deployment
npm run monitoring:deploy -- --profile rieckpil
npm run canary:deploy -- --profile rieckpil
```
