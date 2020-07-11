#!/usr/bin/env bash
set -e
export AWS_PAGER=""

aws cloudformation update-stack \
  --stack-name aws101-container-registry \
  --template-body file://registry.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=RegistryName,ParameterValue=aws101 || true # https://github.com/aws/aws-cli/issues/3625


aws cloudformation wait stack-update-complete --stack-name aws101-container-registry
