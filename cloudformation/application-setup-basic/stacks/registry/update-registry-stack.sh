#!/usr/bin/env bash
export AWS_PAGER=""

aws cloudformation update-stack \
  --stack-name stratospheric-container-registry \
  --template-body file://registry.yml \
  --capabilities CAPABILITY_IAM \
  --parameters \
    ParameterKey=RegistryName,ParameterValue=stratospheric 2> update_error

# Sadly, the AWS CLI returns an error when no update is to be performed. But we want to
# call it a success if the stack is up-to-date. So, we have to work around this by
# catching and grepping the error output. See https://github.com/aws/aws-cli/issues/3625.

grep "No updates are to be performed" update_error
NO_UPDATES=$?
if [ "$NO_UPDATES" -eq 0 ]
then
  echo "Stack is up-to-date! Nothing to do."
  exit 0
fi

grep "error" update_error
ERROR=$?
if [ "$ERROR" -eq 0 ]
then
  echo "Error during stack update!"
  exit 1
fi

aws cloudformation wait stack-update-complete --stack-name stratospheric-container-registry
