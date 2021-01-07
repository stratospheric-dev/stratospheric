#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name stratospheric-cognito \
  --template-body file://cognito.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=ApplicationName,ParameterValue=stratospheric-users \
    ParameterKey=ApplicationUrl,ParameterValue=https://app.stratospheric.dev \
    ParameterKey=LoginPageDomainPrefix,ParameterValue=dev101

aws cloudformation wait stack-create-complete --stack-name stratospheric-cognito
