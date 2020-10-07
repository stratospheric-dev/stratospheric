#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name aws101-cognito \
  --template-body file://cognito.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=AuthName,ParameterValue=aws101-users \
    ParameterKey=ExternalUrl,ParameterValue=https://app.stratospheric.dev \

aws cloudformation wait stack-create-complete --stack-name aws101-cognito
