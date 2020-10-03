# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

EXTERNAL_URL=$(
  aws cloudformation describe-stacks \
    --endpoint-url http://localhost:4566 \
    --profile localstack \
    --stack-name aws101-application-network \
    --output text \
    --query 'Stacks[0].Outputs[?OutputKey==`ExternalUrl`].OutputValue'
)

TODO_UPDATES_ARN=$(
  aws sns create-topic \
    --endpoint-url http://localhost:4566 \
    --profile localstack \
    --name stratospheric-todo-updates \
    --output text \
    --query 'TopicArn'
)

aws sns subscribe \
  --endpoint-url http://localhost:4566 \
  --profile localstack \
  --topic-arn $TOPIC_ARN \
  --protocol http \
  --endpoint $EXTERNAL_URL/stratospheric-todo-updates

echo "AWS SNS topic ARN:                  " $TODO_UPDATES_ARN
echo "AWS SNS endpoint:                   " $EXTERNAL_URL "/stratospheric-todo-updates"
