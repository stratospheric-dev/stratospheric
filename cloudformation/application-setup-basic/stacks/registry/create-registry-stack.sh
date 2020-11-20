#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name stratospheric-container-registry \
  --template-body file://registry.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=RegistryName,ParameterValue=stratospheric

aws cloudformation wait stack-create-complete --stack-name stratospheric-container-registry
