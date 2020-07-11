#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation update-stack \
  --stack-name aws101-cognito \
  --template-body file://cognito.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=AuthName,ParameterValue=aws101-users \
    ParameterKey=ExternalUrl,ParameterValue=https://app.aws101.dev || true # https://github.com/aws/aws-cli/issues/3625

aws cloudformation wait stack-update-complete --stack-name aws101-cognito
