# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name stratospheric-ecs-with-loadbalancer-network \
  --template-body file://network.yml \
  --capabilities CAPABILITY_IAM

aws cloudformation wait stack-create-complete --stack-name stratospheric-ecs-with-loadbalancer-network

aws cloudformation create-stack \
  --stack-name stratospheric-ecs-with-loadbalancer-service \
  --template-body file://service.yml \
  --parameters \
      ParameterKey=StackName,ParameterValue=stratospheric-ecs-with-loadbalancer-network \
      ParameterKey=ServiceName,ParameterValue=stratospheric-todo-app-v1 \
      ParameterKey=ImageUrl,ParameterValue=docker.io/stratospheric/todo-app-v1:latest \
      ParameterKey=ContainerPort,ParameterValue=8080 \
      ParameterKey=HealthCheckPath,ParameterValue=/hello \
      ParameterKey=HealthCheckIntervalSeconds,ParameterValue=90

aws cloudformation wait stack-create-complete --stack-name stratospheric-ecs-with-loadbalancer-service

EXTERNAL_URL=$(aws cloudformation describe-stacks --stack-name stratospheric-ecs-with-loadbalancer-network --output text --query 'Stacks[0].Outputs[?OutputKey==`ExternalUrl`].OutputValue | [0]')
echo "You can access your service at $EXTERNAL_URL"
