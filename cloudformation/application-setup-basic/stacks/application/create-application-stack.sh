#!/usr/bin/env bash
set -e
export AWS_PAGER=""

DOCKER_IMAGE_URL=$1
USER_POOL_CLIENT_SECRET=$2

# upload the stack files
aws s3 cp stacks/application/ s3://aws101.dev/stacks/application --recursive

# create the parent stack with all child stacks
aws cloudformation create-stack \
  --stack-name aws101-application-parent \
  --template-body file://stacks/application/application.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=NetworkStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/aws101.dev/stacks/application/network.yml \
    ParameterKey=ServiceStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/aws101.dev/stacks/application/service.yml \
    ParameterKey=ServiceStackImageUrl,ParameterValue=$DOCKER_IMAGE_URL \
    ParameterKey=ServiceStackUserPoolClientSecret,ParameterValue=$USER_POOL_CLIENT_SECRET \
    ParameterKey=RegistryStackName,ParameterValue=aws101-container-registry

# wait for stack to be created
aws cloudformation wait stack-create-complete --stack-name aws101-application-parent
