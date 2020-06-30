# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

REGISTRY_NAME=aws101

aws cloudformation create-stack \
  --stack-name aws101-application-network \
  --template-body file://network.yml \
  --capabilities CAPABILITY_IAM

aws cloudformation wait stack-create-complete --stack-name aws101-application-network

aws cloudformation create-stack \
  --stack-name aws101-container-registry \
  --template-body file://registry.yml \
  --parameters \
      ParameterKey=RegistryName,ParameterValue=$REGISTRY_NAME \
      ParameterKey=StackName,ParameterValue=aws101-application-network \

aws cloudformation wait stack-create-complete --stack-name aws101-container-registry

AUTH_NAME=aws101-users

aws cloudformation create-stack \
  --stack-name aws101-cognito-user-pool \
  --template-body file://cognito.yml \
  --parameters \
      ParameterKey=AuthName,ParameterValue=$AUTH_NAME

aws cloudformation wait stack-create-complete --stack-name aws101-cognito-user-pool

aws cloudformation create-stack \
  --stack-name aws101-application-stack \
  --template-body file://service.yml \
  --parameters \
      ParameterKey=StackName,ParameterValue=aws101-application-network \
      ParameterKey=ServiceName,ParameterValue=aws101-todo-app \
      ParameterKey=ImageUrl,ParameterValue=docker.io/reflectoring/aws-hello-world:latest \
      ParameterKey=ContainerPort,ParameterValue=8080 \
      ParameterKey=HealthCheckPath,ParameterValue=/hello \
      ParameterKey=HealthCheckIntervalSeconds,ParameterValue=90 \
      ParameterKey=AuthName,ParameterValue=$AUTH_NAME

aws cloudformation wait stack-create-complete --stack-name aws101-application-stack

EXTERNAL_URL=$(aws cloudformation describe-stacks --stack-name aws101-application-network --output text --query 'Stacks[0].Outputs[?OutputKey==`ExternalUrl`].OutputValue | [0]')
echo "You can access your service at $EXTERNAL_URL"