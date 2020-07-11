#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation update-stack \
  --stack-name aws101-container-registry \
  --template-body file://stacks/registry/registry.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=RegistryName,ParameterValue=aws101

aws cloudformation wait stack-update-complete --stack-name aws101-container-registry
