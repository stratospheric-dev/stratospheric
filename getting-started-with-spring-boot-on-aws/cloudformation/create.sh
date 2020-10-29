#!/usr/bin/env bash
export AWS_PAGER=""

BUCKET_NAME=$1

aws cloudformation create-stack \
  --stack-name stratospheric-demo-infrastructure \
  --template-body file://application-infrastructure.yml \
  --parameters \
    ParameterKey=BucketName,ParameterValue=$BUCKET_NAME

aws cloudformation wait stack-create-complete --stack-name stratospheric-demo-infrastructure
