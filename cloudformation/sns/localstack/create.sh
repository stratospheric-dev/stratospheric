# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

aws cloudformation create-stack \
  --endpoint-url http://localhost:4566 \
  --profile localstack \
  --stack-name stratospheric-sns-topic \
  --template-body file://../sns.yml

aws cloudformation wait stack-create-complete --stack-name stratospheric-sns-topic

TODO_UPDATES_ARN=$(aws cloudformation describe-stacks --stack-name stratospheric-sns-topic --output text --query 'Stacks[0].Outputs[?OutputKey==`TodoUpdatesARN`].OutputValue')

echo "AWS SNS ARN:                  " $TODO_UPDATES_ARN
