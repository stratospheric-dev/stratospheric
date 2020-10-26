#!/usr/bin/env bash
export AWS_PAGER=""

aws cloudformation create-stack \
  --stack-name stratospheric-demo-infrastructure \
  --template-body file://application-infrastructure.yml \
  --capabilities CAPABILITY_IAM

aws cloudformation wait stack-create-complete --stack-name stratospheric-demo-infrastructure
