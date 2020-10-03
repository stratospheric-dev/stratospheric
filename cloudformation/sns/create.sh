# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

EXTERNAL_URL=$(
  aws cloudformation describe-stacks \
    --stack-name aws101-application-network \
    --output text \
    --query 'Stacks[0].Outputs[?OutputKey==`ExternalUrl`].OutputValue'
)

aws cloudformation create-stack \
  --stack-name stratospheric-sns-topic \
  --template-body file://sns.yml

aws cloudformation wait stack-create-complete --stack-name stratospheric-sns-topic

TODO_UPDATES_ARN=$(
  aws cloudformation describe-stacks \
    --stack-name stratospheric-sns-topic \
    --output text \
    --query 'Stacks[0].Outputs[?OutputKey==`TodoUpdatesARN`].OutputValue'
)

aws sns subscribe \
  --topic-arn $TODO_UPDATES_ARN \
  --protocol https \
  --endpoint $EXTERNAL_URL/stratospheric-todo-updates

echo "AWS SNS topic ARN:                  " $TODO_UPDATES_ARN
echo "AWS SNS endpoint:                   " $EXTERNAL_URL "/stratospheric-todo-updates"
