#!/usr/bin/env bash
export AWS_PAGER=""

# creating the registry stack if it's not running already
./stack-exists.sh "aws101-registry"
stack_exists=$?
if [ "$stack_exists" -eq 0 ]
then
  echo "Registry stack is already running...nothing to do!"
else
  echo "Registry stack not running yet. Creating it now..."
  ./create-registry-stack.sh
  echo "Successfully created the registry stack!"
fi