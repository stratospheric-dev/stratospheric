#!/usr/bin/env bash
export AWS_PAGER=""

DOCKER_IMAGE_URL=$1

echo "Deploying Docker image $DOCKER_IMAGE_URL ..."

USER_POOL_ID=$(aws cloudformation describe-stacks --stack-name stratospheric-cognito --output text --query 'Stacks[0].Outputs[?OutputKey==`UserPoolId`].OutputValue')
USER_POOL_CLIENT_ID=$(aws cloudformation describe-stacks --stack-name stratospheric-cognito --output text --query 'Stacks[0].Outputs[?OutputKey==`UserPoolClientId`].OutputValue')
USER_POOL_CLIENT_SECRET=$(aws cognito-idp describe-user-pool-client --user-pool-id $USER_POOL_ID --client-id $USER_POOL_CLIENT_ID --output text --query 'UserPoolClient.ClientSecret')

../../stack-exists.sh "stratospheric-application-parent"
stack_exists=$?

if [ "$stack_exists" -eq 0 ]
then
  ../../stack-update-in-progress.sh "stratospheric-application-parent"
  update_in_progress=$?
  if [ "$update_in_progress" -eq 0 ]
  then
    echo "Application stack is currently updating. Skipping the update"
  else
    echo "Application stack is already running. Updating it now..."
    ./update-application-stack.sh $DOCKER_IMAGE_URL $USER_POOL_CLIENT_SECRET || exit 1
    echo "Successfully updated the application stack!"
  fi
else
  echo "Application stack is not running. Creating it now..."
  ./create-application-stack.sh $DOCKER_IMAGE_URL $USER_POOL_CLIENT_SECRET || exit 1
  echo "Successfully created the application stack!"
fi
