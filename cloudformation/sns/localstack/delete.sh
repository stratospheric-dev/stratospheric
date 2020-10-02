# Turning off the AWS pager so that the CLI doesn't open an editor for each command result
export AWS_PAGER=""

aws cloudformation delete-stack \
  --endpoint-url http://localhost:4566 \
  --profile localstack \
  --stack-name stratospheric-sns-topic
