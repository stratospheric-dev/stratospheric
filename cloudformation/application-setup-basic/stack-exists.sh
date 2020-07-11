#!/usr/bin/env bash
export AWS_PAGER=""

stack_name=$1

existing_stacks=$(aws cloudformation list-stacks \
  --stack-status-filter CREATE_COMPLETE UPDATE_COMPLETE \
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