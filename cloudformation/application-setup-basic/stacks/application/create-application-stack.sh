#!/usr/bin/env bash
set -e
export AWS_PAGER=""

DOCKER_IMAGE_URL=$1
USER_POOL_CLIENT_SECRET=$2

# upload the stack files
aws s3 cp ./ s3://stratospheric.dev/stacks/application --recursive

# create the parent stack with all child stacks
aws cloudformation create-stack \
  --stack-name stratospheric-application-parent \
  --template-body file://application.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=NetworkStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/stratospheric.dev/stacks/application/network.yml \
    ParameterKey=MessagingStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/stratospheric.dev/stacks/application/messaging.yml \
    ParameterKey=ServiceStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/stratospheric.dev/stacks/application/service.yml \
    ParameterKey=ServiceStackImageUrl,ParameterValue=$DOCKER_IMAGE_URL \
    ParameterKey=ServiceStackUserPoolClientSecret,ParameterValue=$USER_POOL_CLIENT_SECRET \
    ParameterKey=RegistryStackName,ParameterValue=stratospheric-container-registry \
    ParameterKey=DatabaseStackTemplateUrl,ParameterValue=https://s3.amazonaws.com/stratospheric.dev/stacks/application/database.yml \
    ParameterKey=DatabaseStackDBUsername,ParameterValue=stratospheric \
    ParameterKey=DatabaseStackDBName,ParameterValue=stratospheric

# wait for stack to be created
aws cloudformation wait stack-create-complete --stack-name stratospheric-application-parent
