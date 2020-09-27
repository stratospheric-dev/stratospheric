# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

EXTERNAL_URL=$(aws cloudformation describe-stacks --stack-name stratospheric-application-network --output text --query 'Stacks[0].Outputs[?OutputKey==`ExternalUrl`].OutputValue')
APP_URL=https://app.stratospheric.dev

aws cloudformation create-stack \
  --stack-name stratospheric-sns-topic \
  --template-body file://sns.yml \
  --parameters \
      ParameterKey=AuthName,ParameterValue=$AUTH_NAME \
      ParameterKey=ExternalUrl,ParameterValue=$APP_URL

aws cloudformation wait stack-create-complete --stack-name stratospheric-cognito-topic

USER_POOL_ID=$(aws cloudformation describe-stacks --stack-name stratospheric-cognito-topic --output text --query 'Stacks[0].Outputs[?OutputKey==`UserPoolId`].OutputValue')
USER_POOL_CLIENT_ID=$(aws cloudformation describe-stacks --stack-name stratospheric-cognito-topic --output text --query 'Stacks[0].Outputs[?OutputKey==`UserPoolClientId`].OutputValue')

echo "AWS Cognito UserPool ID:                  " $USER_POOL_ID
echo "AWS Cognito UserPool client ID:           " $USER_POOL_CLIENT_ID
echo "AWS Cognito UserPool client secret:       " $USER_POOL_CLIENT_SECRET
