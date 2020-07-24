#!/usr/bin/env bash
# Script to determine whether a CloudFormation stack is up and running or not.
# Returns response code 0 if the stack exists and response code 1 if the stack does not exist.
# Usage: ./stack-exists.sh <stack_name>

set -e
export AWS_PAGER=""

stack_name=$1

existing_stacks=$(aws cloudformation list-stacks \
  --stack-status-filter CREATE_COMPLETE UPDATE_COMPLETE UPDATE_ROLLBACK_COMPLETE \
  --output text \
  --query "StackSummaries[].[StackName]")

stack_in_list=$(echo "$existing_stacks" | grep "$stack_name" || true)

if [ -z "$stack_in_list" ]
then
  echo "Did not find stack $stack_name"
  exit 1
else
  echo "Found stack $stack_name"
  exit 0
fi