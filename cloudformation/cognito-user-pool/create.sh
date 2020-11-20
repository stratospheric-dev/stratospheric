# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

AUTH_NAME=stratospheric-users
EXTERNAL_URL=$(aws cloudformation describe-stacks --stack-name stratospheric-application-network --output text --query 'Stacks[0].Outputs[?OutputKey==`ExternalUrl`].OutputValue')
APP_URL=https://app.stratospheric.dev

aws cloudformation create-stack \
  --stack-name stratospheric-cognito-user-pool \
  --template-body file://cognito.yml \
  --parameters \
      ParameterKey=AuthName,ParameterValue=$AUTH_NAME \
      ParameterKey=ExternalUrl,ParameterValue=$APP_URL

aws cloudformation wait stack-create-complete --stack-name stratospheric-cognito-user-pool

USER_POOL_ID=$(aws cloudformation describe-stacks --stack-name stratospheric-cognito-user-pool --output text --query 'Stacks[0].Outputs[?OutputKey==`UserPoolId`].OutputValue')
USER_POOL_CLIENT_ID=$(aws cloudformation describe-stacks --stack-name stratospheric-cognito-user-pool --output text --query 'Stacks[0].Outputs[?OutputKey==`UserPoolClientId`].OutputValue')
USER_POOL_CLIENT_SECRET=$(aws cognito-idp describe-user-pool-client --user-pool-id $USER_POOL_ID --client-id $USER_POOL_CLIENT_ID --output text --query 'UserPoolClient.ClientSecret')

echo "AWS Cognito UserPool ID:                  " $USER_POOL_ID
echo "AWS Cognito UserPool client ID:           " $USER_POOL_CLIENT_ID
echo "AWS Cognito UserPool client secret:       " $USER_POOL_CLIENT_SECRET
