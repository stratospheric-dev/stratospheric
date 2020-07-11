#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name aws101-container-registry \
  --template-body file://stacks/registry/registry.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=RegistryName,ParameterValue=aws101

aws cloudformation wait stack-create-complete --stack-name aws101-container-registry
