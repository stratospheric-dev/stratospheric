#!/usr/bin/env bash
export AWS_PAGER=""

../../stack-exists.sh "stratospheric-cognito"
stack_exists=$?
if [ "$stack_exists" -eq 0 ]
then
  echo "Cognito stack is already running. Updating it now..."
  ./update-cognito-stack.sh || exit 1
  echo "Successfully updated the Cognito stack!"
else
  echo "Cognito stack is not running. Creating it now..."
  ./create-cognito-stack.sh || exit 1
  echo "Successfully created the Cognito stack!"
fi
