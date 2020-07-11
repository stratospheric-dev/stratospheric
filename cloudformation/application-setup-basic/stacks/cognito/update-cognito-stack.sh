#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation update-stack \
  --stack-name aws101-cognito \
  --template-body file://stacks/cognito/cognito.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=AuthName,ParameterValue=aws101-users \
    ParameterKey=ExternalUrl,ParameterValue=https://app.aws101.dev \

aws cloudformation wait stack-update-complete --stack-name aws101-cognito
