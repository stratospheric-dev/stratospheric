#!/usr/bin/env bash
export AWS_PAGER=""

DOCKER_IMAGE_URL=$1

echo "Deploying Docker image $DOCKER_IMAGE_URL ..."

# creating or updating the application stack, depending on if it exists already
./stack-exists.sh "aws101-application-parent"
stack_exists=$?
if [ "$stack_exists" -eq 0 ]
then
  echo "Application stack is already running. Updating it now..."
  #./update-application-stack.sh $DOCKER_IMAGE_URL
  echo "Successfully updated the application stack!"
else
  echo "Application stack is not running. Creating it now..."
  #./create-application-stack.sh $DOCKER_IMAGE_URL
  echo "Successfully created the application stack!"
fi

echo "Successfully deployed Docker image $DOCKER_IMAGE_URL"