#!/usr/bin/env bash
export AWS_PAGER=""

../../stack-exists.sh "stratospheric-container-registry"
stack_exists=$?
if [ "$stack_exists" -eq 0 ]
then
  echo "Registry stack is already running. Updating it now...!"
  ./update-registry-stack.sh || exit 1
  echo "Successfully updated the registry stack!"
else
  echo "Registry stack not running yet. Creating it now..."
  ./create-registry-stack.sh || exit 1
  echo "Successfully created the registry stack!"
fi
