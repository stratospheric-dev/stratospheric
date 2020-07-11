export AWS_PAGER=""

DOCKER_IMAGE_URL=$1

# get ECS role from registry stack (registry stack must be up and running!)
ECS_TASK_ROLE=$(aws cloudformation describe-stacks --stack-name aws101-registry --output text --query 'Stacks[0].Outputs[?OutputKey==`ECSTaskExecutionRole`].OutputValue | [0]')

# upload the stack files
aws s3 cp stacks/application/ s3://aws101.dev/stacks/application --recursive

# create the parent stack with all child stacks
aws cloudformation create-stack \
  --stack-name aws101-application-parent \
  --template-body file://stacks/application/application.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=NetworkStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/aws101.dev/stacks/application/network.yml \
    ParameterKey=CognitoStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/aws101.dev/stacks/application/cognito.yml \
    ParameterKey=CognitoStackAuthName,ParameterValue=aws101-users \
    ParameterKey=CognitoStackExternalUrl,ParameterValue=https://app.aws101.dev \
    ParameterKey=ServiceStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/aws101.dev/stacks/application/service.yml \
    ParameterKey=ServiceStackImageUrl,ParameterValue=DOCKER_IMAGE_URL \
    ParameterKey=ServiceStackECSTaskExecutionRoleArn,ParameterValue=$ECS_TASK_ROLE

# wait for stack to be created
aws cloudformation wait stack-create-complete --stack-name aws101-application-parent
