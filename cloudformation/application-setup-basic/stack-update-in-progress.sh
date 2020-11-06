#!/usr/bin/env bash
# Script to determine whether a CloudFormation stack is currently in UPDATE_IN_PROGRESS state
# Returns response code 0 if the stack is updating and response code 1 if the stack is not updating.
# Usage: ./stack-update-in-progress.sh <stack_name>

set -e
export AWS_PAGER=""

stack_name=$1

last_stack_event=$(aws cloudformation describe-stack-events \
  --max-items 1 \
  --stack-name "$stack_name" \
  --output text \
  --query "StackEvents[].ResourceStatus")

stack_is_updating=$(echo "$last_stack_event" | grep "UPDATE_IN_PROGRESS" || true)

if [ -z "$stack_is_updating" ]
then
  echo "Stack $stack_name is not in UPDATE_IN_PROGRESS"
  exit 1
else
  echo "Stack $stack_name is in UPDATE_IN_PROGRESS"
  exit 0
fi
