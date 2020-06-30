# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

AUTH_NAME=aws101-users

aws cloudformation create-stack \
  --stack-name aws101-cognito-user-pool \
  --template-body file://cognito.yml \
  --parameters \
      ParameterKey=AuthName,ParameterValue=$AUTH_NAME

aws cloudformation wait stack-create-complete --stack-name aws101-cognito-user-pool
